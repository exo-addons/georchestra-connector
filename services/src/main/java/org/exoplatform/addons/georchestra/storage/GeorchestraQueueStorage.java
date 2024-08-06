package org.exoplatform.addons.georchestra.storage;

import org.exoplatform.addons.georchestra.storage.dao.GeorchestraQueueDAO;
import org.exoplatform.addons.georchestra.storage.entity.GeorchestraQueueEntity;
import org.exoplatform.addons.georchestra.storage.model.GeorchestraQueue;
import org.exoplatform.addons.georchestra.storage.model.GeorchestraQueueType;

import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.stream.Collectors;

public class GeorchestraQueueStorage {

  private GeorchestraQueueDAO georchestraQueueDAO;

  public GeorchestraQueueStorage(GeorchestraQueueDAO georchestraQueueDAO) {
    this.georchestraQueueDAO = georchestraQueueDAO;
  }

  public void add(GeorchestraQueue georchestraQueue) {
    GeorchestraQueueEntity entity = buildEntity(georchestraQueue);
    georchestraQueueDAO.create(entity);
  }

  private GeorchestraQueueEntity buildEntity(GeorchestraQueue georchestraQueue) {
    GeorchestraQueueEntity georchestraQueueEntity = new GeorchestraQueueEntity();
    georchestraQueueEntity.setId(georchestraQueue.getId());
    georchestraQueueEntity.setSpaceId(georchestraQueue.getSpaceId());
    georchestraQueueEntity.setUserId(georchestraQueue.getUserId());
    georchestraQueueEntity.setType(georchestraQueue.getType().name());
    return georchestraQueueEntity;
  }

  public int getQueueLengthForSpace(String spaceId) {
    return georchestraQueueDAO.countQueueItemForSpace(spaceId);
  }

  public List<GeorchestraQueue> findGeorchestraQueue(int offset, int limit) {
    return georchestraQueueDAO.findGeorchestraQueue(offset, limit)
                       .stream()
                       .map(this::fillFromEntity)
                       .collect(Collectors.toList());
  }

  private GeorchestraQueue fillFromEntity(GeorchestraQueueEntity entity) {
    if (entity == null) {
      return  null;
    }
    GeorchestraQueue georchestraQueue = new GeorchestraQueue(entity.getSpaceId(), entity.getUserId(), GeorchestraQueueType.valueOf(entity.getType()));
    georchestraQueue.setId(entity.getId());
    return georchestraQueue;
  }


  public void deleteAllGeorchestraQueue(List<GeorchestraQueue> itemsToRemove) {
    itemsToRemove.stream().forEach(georchestraQueue -> georchestraQueueDAO.deleteById(georchestraQueue.getId()));


  }
}
