package org.exoplatform.addons.georchestra.storage.dao;

import org.exoplatform.addons.georchestra.storage.entity.GeorchestraQueueEntity;
import org.exoplatform.commons.api.persistence.ExoTransactional;
import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

public class GeorchestraQueueDAO extends GenericDAOJPAImpl<GeorchestraQueueEntity, Long> {

  public int countQueueItemForSpace(String spaceId) {
    return getEntityManager().createNamedQuery("GeorchestraQueueEntity.countItemBySpaceId", Long.class)
      .setParameter("spaceId", Long.parseLong(spaceId))
      .getSingleResult()
      .intValue();
  }

  public List<GeorchestraQueueEntity> findGeorchestraQueue(int offset, int limit) {
    TypedQuery<GeorchestraQueueEntity> query =
        getEntityManager().createNamedQuery("GeorchestraQueueEntity.findFirstGeorchestraQueue",
                                            GeorchestraQueueEntity.class);
    query.setMaxResults(limit);
    query.setFirstResult(offset);
    try {
      return query.getResultList();
    } catch (NoResultException ex) {
      return new ArrayList<>();
    }
  }

  @ExoTransactional
  public void deleteById(long id) {
    getEntityManager().createNamedQuery("GeorchestraQueueEntity.deleteById")
                      .setParameter("id", id).executeUpdate();
  }
}
