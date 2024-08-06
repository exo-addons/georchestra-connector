package org.exoplatform.addons.georchestra.listeners;

import org.exoplatform.addons.georchestra.services.GeorchestraService;
import org.exoplatform.addons.georchestra.storage.model.GeorchestraQueue;
import org.exoplatform.addons.georchestra.storage.model.GeorchestraQueueType;
import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.api.settings.SettingValue;
import org.exoplatform.commons.api.settings.data.Context;
import org.exoplatform.commons.api.settings.data.Scope;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.space.SpaceListenerPlugin;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceLifeCycleEvent;

public class GeorchestraSpaceListener extends SpaceListenerPlugin {
  private static final Log LOG = ExoLogger.getLogger(GeorchestraSpaceListener.class);

  private GeorchestraService georchestraService;
  private SettingService     settingsService;
  public static final String GEORCHESTRA_ROLE = "geOrchestraRole";

  public GeorchestraSpaceListener(GeorchestraService georchestraService, SettingService settingsService) {
    this.georchestraService = georchestraService;
    this.settingsService = settingsService;
  }

  @Override
  public void joined(SpaceLifeCycleEvent event) {
    String username = event.getTarget();
    Space space = event.getSpace();

    SettingValue
        setting = settingsService.get(Context.GLOBAL, Scope.SPACE.id(String.valueOf(space.getId())), GEORCHESTRA_ROLE);
    if (setting != null) {
      GeorchestraQueue georchestraQueue = new GeorchestraQueue(Long.parseLong(space.getId()), username, GeorchestraQueueType.ADD);
      georchestraService.addInQueue(georchestraQueue);
    }

  }

  @Override
  public void left(SpaceLifeCycleEvent event) {
    String username = event.getTarget();
    Space space = event.getSpace();
    SettingValue
        setting = settingsService.get(Context.GLOBAL, Scope.SPACE.id(String.valueOf(space.getId())), GEORCHESTRA_ROLE);
    if (setting != null) {
      GeorchestraQueue georchestraQueue = new GeorchestraQueue(Long.parseLong(space.getId()), username, GeorchestraQueueType.REMOVE);
      georchestraService.addInQueue(georchestraQueue);
    }

  }

}
