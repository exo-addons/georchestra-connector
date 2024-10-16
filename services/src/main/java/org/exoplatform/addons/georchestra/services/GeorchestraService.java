package org.exoplatform.addons.georchestra.services;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.exoplatform.addons.georchestra.storage.GeorchestraQueueStorage;
import org.exoplatform.addons.georchestra.storage.model.GeorchestraQueue;
import org.exoplatform.addons.georchestra.storage.model.GeorchestraQueueType;
import org.exoplatform.commons.api.settings.SettingValue;
import org.exoplatform.commons.api.settings.data.Context;
import org.exoplatform.commons.api.settings.data.Scope;
import org.exoplatform.commons.persistence.impl.EntityManagerHolder;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.settings.jpa.entity.ContextEntity;
import org.exoplatform.settings.jpa.entity.SettingsEntity;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.commons.api.settings.SettingService;

import org.apache.http.client.HttpClient;
import org.exoplatform.social.core.space.spi.SpaceService;

import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.TypedQuery;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class GeorchestraService {
  private static final Log LOG = ExoLogger.getLogger(GeorchestraService.class);

  public static final String GEORCHESTRA_ROLE = "geOrchestraRole";

  private static final int        DEFAULT_POOL_CONNECTION = 20;

  private static final String GEOCHESTRA_SERVICE_API = "georchestra";

  private              HttpClient httpClient;

  private GeorchestraQueueStorage georchestraQueueStorage;

  private SettingService settingsService;
  private SpaceService   spaceService;
  private IdentityManager identityManager;

  private String georchestraUrl;
  private String georchestraUsername="";
  private String georchestraPassword="";
  private String basicToken;


  public GeorchestraService(InitParams initParams,
                            GeorchestraQueueStorage georchestraQueueStorage,
                            SettingService settingsService,
                            SpaceService spaceService,
                            IdentityManager identityManager) {
    this.georchestraQueueStorage = georchestraQueueStorage;
    this.settingsService = settingsService;
    this.spaceService = spaceService;
    this.identityManager = identityManager;
    if (initParams.get("url")!=null) {
      this.georchestraUrl=initParams.getValueParam("url").getValue();
    }
    if (initParams.get("username")!=null) {
      this.georchestraUsername=initParams.getValueParam("username").getValue();
    }
    if (initParams.get("password")!=null) {
      this.georchestraPassword=initParams.getValueParam("password").getValue();
    }
    basicToken = Base64.getEncoder().encodeToString((georchestraUsername+":"+georchestraPassword).getBytes());

    PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
    connectionManager.setDefaultMaxPerRoute(DEFAULT_POOL_CONNECTION);
    HttpClientBuilder httpClientBuilder = HttpClients.custom()
                                                     .setConnectionManager(connectionManager)
                                                     .setConnectionReuseStrategy(new DefaultConnectionReuseStrategy())
                                                     .setMaxConnPerRoute(DEFAULT_POOL_CONNECTION);
    this.httpClient = httpClientBuilder.build();
  }

  public void addInQueue(GeorchestraQueue georchestraQueue) {
    georchestraQueueStorage.add(georchestraQueue);
  }

  public boolean validateRole(String role) {
    HttpGet httpGet = initHttpGet("/console/private/roles/"+role);
    long startTime = System.currentTimeMillis();
    try {
      HttpResponse httpResponse=httpClient.execute(httpGet);
      new BasicResponseHandler().handleResponse(httpResponse);
      return true;
    } catch (HttpResponseException http) {
      LOG.error("remote_service={} operation={} parameters=\"role:{}\", status=ko "
                    + "duration_ms={} error_msg=\"{}, status : {} \"",
                GEOCHESTRA_SERVICE_API,
                "testRole",
                role,
                System.currentTimeMillis() - startTime,
                http.getReasonPhrase(),
                http.getStatusCode());
    } catch (IOException e) {
      LOG.error("remote_service={} operation={} parameters=\"role:{}\", status=ko "
                    + "duration_ms={} error_msg=\"{}\"",
                GEOCHESTRA_SERVICE_API,
                "testRole",
                role,
                System.currentTimeMillis() - startTime,
                e.getCause(),e);
    }
    return false;

  }

  public boolean saveRole(String role, Space space) {
    if (this.validateRole(role)) {
      settingsService.set(Context.GLOBAL, Scope.SPACE.id(space.getId()), GEORCHESTRA_ROLE, new SettingValue<>(role));
      Arrays.stream(space.getMembers()).forEach(username -> {
        GeorchestraQueue
            georchestraQueue =
            new GeorchestraQueue(Long.parseLong(space.getId()), username, GeorchestraQueueType.ADD);
        addInQueue(georchestraQueue);
      });
      return true;
    } else {
      return false;
    }
  }

  public String getRole(Space space) {
    SettingValue<?> setting = settingsService.get(Context.GLOBAL, Scope.SPACE.id(space.getId()), GEORCHESTRA_ROLE);
    return setting == null ? null : setting.getValue().toString();

  }

  public void deleteRole(Space space) {
    settingsService.remove(Context.GLOBAL, Scope.SPACE.id(space.getId()), GEORCHESTRA_ROLE);
  }

  public int getQueueLengthForSpace(Space space) {
    return georchestraQueueStorage.getQueueLengthForSpace(space.getId());
  }

  private HttpGet initHttpGet(String uri) {
    HttpGet httpTypeRequest = new HttpGet(georchestraUrl + uri);
    httpTypeRequest.setHeader("Content-Type", "application/json");
    httpTypeRequest.setHeader("Authorization", "Basic "+basicToken);
    return httpTypeRequest;
  }

  private HttpPost initHttpPost(String uri) {
    HttpPost httpTypeRequest = new HttpPost(georchestraUrl + uri);
    httpTypeRequest.setHeader("Content-Type", "application/json");
    httpTypeRequest.setHeader("Authorization", "Basic "+basicToken);
    return httpTypeRequest;
  }

  public List<GeorchestraQueue> findGeorchestraQueue(int offset, int limit) {
    return georchestraQueueStorage.findGeorchestraQueue(offset,limit);
  }

  public boolean treatQueueItem(GeorchestraQueue item) {
    HttpPost httpPost = initHttpPost("/console/private/roles_users");
    long startTime = System.currentTimeMillis();

    SettingValue setting = settingsService.get(Context.GLOBAL, Scope.SPACE.id(String.valueOf(item.getSpaceId())), GEORCHESTRA_ROLE);
    if (setting == null) {
      //role remove from space before doing the action.
      //ignore it
      return true;
    }
    String data="";
    try {

      data = "{\"users\":[\""+item.getUserId()+"\"],";

      if (item.getType().equals(GeorchestraQueueType.ADD)) {
        data+="\"PUT\":[\""+setting.getValue().toString()+"\"],";
        data+="\"DELETE\":[]";
      } else if (item.getType().equals(GeorchestraQueueType.REMOVE)) {
        data+="\"PUT\":[],";
        data+="\"DELETE\":[\""+setting.getValue().toString()+"\"]";
      }
      data+="}";

      //pass the json string request in the entity
      HttpEntity entity = new ByteArrayEntity(data.getBytes("UTF-8"));
      httpPost.setEntity(entity);

      HttpResponse httpResponse = httpClient.execute(httpPost);
      new BasicResponseHandler().handleResponse(httpResponse);
      return true;
    } catch (HttpResponseException http) {
      if (http.getStatusCode() == 404) {
        //user not found
        //ignore it
        return true;
      } else {
        LOG.error("remote_service={} operation={} parameters=\"item:{}, data:{}\", status=ko "
                      + "duration_ms={} error_msg=\"{}, status : {} \"",
                  GEOCHESTRA_SERVICE_API,
                  "add/remove role",
                  item.toString(),
                  data,
                  System.currentTimeMillis() - startTime,
                  http.getReasonPhrase(),
                  http.getStatusCode());
      }
    } catch (IOException e) {
      LOG.error("remote_service={} operation={} parameters=\"item:{}\", status=ko "
                    + "duration_ms={} error_msg=\"{}\"",
                GEOCHESTRA_SERVICE_API,
                "add/remove role",
                item.toString(),
                System.currentTimeMillis() - startTime,
                e.getCause(),e);
    }
    return false;
  }

  public void deleteAllGeorchestraQueue(List<GeorchestraQueue> itemsToRemove) {
    georchestraQueueStorage.deleteAllGeorchestraQueue(itemsToRemove);
  }

  public boolean addUserInSpacesByRole(String username, String role) {
    LOG.debug("Try to add user {} in spaces with role {}",username,role);
    Identity userIdentity = identityManager.getOrCreateUserIdentity(username);
    if (userIdentity == null) {
      LOG.debug("User {} not found in eXo", username);
      return false;
    }

    List<SettingsEntity> results = getSettingsEntities(role);
    results.forEach(settingsEntity -> {
      String spaceId = settingsEntity.getScope().getName();
      Space space = spaceService.getSpaceById(spaceId);
      LOG.debug("Add user {} as member of space {}", username, space.getDisplayName());
      spaceService.addMember(space,username);
    });
    return true;

  }

  public boolean removeUserInSpacesByRole(String username, String role) {
    Identity userIdentity = identityManager.getOrCreateUserIdentity(username);
    if (userIdentity == null) {
      return false;
    }
    List<SettingsEntity> results = getSettingsEntities(role);
    results.forEach(settingsEntity -> {
      String spaceId = settingsEntity.getScope().getName();
      Space space = spaceService.getSpaceById(spaceId);
      if (!spaceService.isManager(space,username) || space.getManagers().length != 1) {
        spaceService.removeMember(space, username);
      } else {
        LOG.warn("User {} should be removed from space {}, but he is last manager. Ignore it.", username, space.getPrettyName());
      }
    });
    return true;

  }

  private List<SettingsEntity> getSettingsEntities(String role) {
    String sql = "SELECT distinct(s) FROM SettingsEntity s " +
        "JOIN s.context c " +
        "JOIN s.scope sc " +
        "WHERE sc.type = :scopeType " +
        "AND c.type = :contextType " +
        "AND s.name = :settingName " +
        "AND s.value = :settingValue ";

    EntityManager entityManager = EntityManagerHolder.get();
    TypedQuery<SettingsEntity> query = entityManager.createQuery(sql, SettingsEntity.class);
    query.setParameter("scopeType","SPACE");
    query.setParameter("contextType","GLOBAL");
    query.setParameter("settingName",GEORCHESTRA_ROLE);
    query.setParameter("settingValue", role);

    return query.getResultList();
  }
}
