package org.exoplatform.addons.georchestra.storage.entity;

import javax.persistence.*;
import org.exoplatform.commons.api.persistence.ExoEntity;

@Entity(name = "GeorchestraQueueEntity")
@ExoEntity
@Table(name = "GEORCHESTRA_QUEUE")
@NamedQueries({
    @NamedQuery(name = "GeorchestraQueueEntity.countItemBySpaceId", query = "SELECT COUNT(*) "
        + " FROM GeorchestraQueueEntity georchestraQueueEntity " + " WHERE georchestraQueueEntity.spaceId = :spaceId"),
    @NamedQuery(name = "GeorchestraQueueEntity.findFirstGeorchestraQueue", query = "SELECT q FROM GeorchestraQueueEntity q "
        + " ORDER BY q.id ASC"),
    @NamedQuery(name = "GeorchestraQueueEntity.deleteById", query = "DELETE FROM GeorchestraQueueEntity q "
        + " WHERE q.id = :id")})
public class GeorchestraQueueEntity {
  @Id
  @Column(name = "ID")
  @SequenceGenerator(name="SEQ_GEORCHESTRA_QUEUE", sequenceName="SEQ_GEORCHESTRA_QUEUE", allocationSize = 1)
  @GeneratedValue(strategy= GenerationType.AUTO, generator="SEQ_GEORCHESTRA_QUEUE")
  private long id;

  @Column(name = "USER_ID")
  private String userId;

  @Column(name = "SPACE_ID")
  private Long spaceId;

  @Column(name = "TYPE")
  private String type;



  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public Long getSpaceId() {
    return spaceId;
  }

  public void setSpaceId(Long spaceId) {
    this.spaceId = spaceId;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
