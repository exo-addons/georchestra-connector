export function validateRole(role,spaceId) {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/georchestra/validate?role=${role}&spaceId=${spaceId}`, {
    credentials: 'include',
    method: 'GET'
  }).then(resp => {
    if (resp.ok) {
      return resp.json();
    } else {
      throw new Error('Error when checking gerOrchestra role');
    }
  });
}


export function saveRole(role,spaceId) {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/georchestra/save?role=${role}&spaceId=${spaceId}`, {
    credentials: 'include',
    method: 'POST'
  }).then(resp => {
    if (resp.ok) {
      return resp;
    } else {
      throw new Error('Error when saving gerOrchestra role');
    }
  });
}
export function deleteRole(role,spaceId) {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/georchestra/delete?spaceId=${spaceId}`, {
    credentials: 'include',
    method: 'DELETE'
  }).then(resp => {
    if (resp.ok) {
      return resp;
    } else {
      throw new Error('Error when deleting gerOrchestra role');
    }
  });
}

export function getRole(spaceId) {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/georchestra/getRole/${spaceId}`, {
    credentials: 'include',
    method: 'GET'
  }).then(resp => {
    if (resp.ok) {
      return resp.json();
    } else {
      throw new Error('Error when getting gerOrchestra role');
    }
  });
}


export function getNbItemInQueueForSpace(spaceId) {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/georchestra/queue/${spaceId}`, {
    credentials: 'include',
    method: 'GET'
  }).then(resp => {
    if (resp.ok) {
      return resp.json();
    } else {
      throw new Error('Error when getting nbItem in queue.');
    }
  });
}
