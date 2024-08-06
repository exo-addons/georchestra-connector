package org.exoplatform.addons.georchestra.storage.model;

public class GeorchestraQueue {

  private long id;
  private long spaceId;

  private String userId;

  @Override
  public String toString() {
    return "GeorchestraQueue{" +
        "id=" + id +
        ", spaceId=" + spaceId +
        ", userId='" + userId + '\'' +
        ", type=" + type +
        '}';
  }

  private GeorchestraQueueType type;

  public GeorchestraQueue(long spaceId, String userId, GeorchestraQueueType type) {
    this.spaceId = spaceId;
    this.userId = userId;
    this.type = type;
  }

  public long getSpaceId() {
    return spaceId;
  }

  public void setSpaceId(long spaceId) {
    this.spaceId = spaceId;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public GeorchestraQueueType getType() {
    return type;
  }

  public void setType(GeorchestraQueueType type) {
    this.type = type;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }
}
