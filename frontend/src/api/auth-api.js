import requester from "../hooks/requester";

const BASE_URL = "http://localhost:8080";

export const login = (email, password) =>
  requester.post(`${BASE_URL}/auth/login`, { email, password });

export const createUser = (username, email, password, roleName, token) =>
  requester.post(
    `${BASE_URL}/users`,
    { username, email, password, roleName },
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    },
  );

// -----------USER----------------------
export const getAllUsers = async () => {
  const result = await requester.get(`${BASE_URL}/users`);
  return result;
};

export const getOneUser = async (userId) => {
  const result = await requester.get(`${BASE_URL}/users/${userId}`);
  return result;
};

export const deleteUser = async (userId) => {
  const result = await requester.del(`${BASE_URL}/users/${userId}`);
  return result;
};

export const changeUserRole = async (userId, roleName) => {
  const result = await requester.post(`${BASE_URL}/users/change-role`, {
    id: userId,
    roleName: roleName,
  });
  return result;
};


// -----------------DOC----------------------------------
export const createDocument = async (values) => {
  const formData = new FormData();

  formData.append("title", values.title);
  formData.append("description", values.description);
  formData.append("file", values.file);
  formData.append("extension", values.extension);

  const result = await requester.post(`${BASE_URL}/documents`, formData, {
    header: {
      "Content-Type": "multipart/form-data",
    },
  });
  return result;
};


export const getAllDocuments = async () => {
  const result = await requester.get(`${BASE_URL}/documents`);
  return result;
}

export const getLatesVersion = async (documentId) => {
  const result = await requester.get(`${BASE_URL}/versions/document/${documentId}/latest`);
  return result;
}

// -----------------VERSIONS----------------------

export const createNewVersion = async (documentId,file,extension,changeSummary) => {
  const formData = new FormData();

  formData.append("documentId",documentId);
  formData.append("file",file);
  formData.append("extension",extension);
  formData.append("changeSummary",changeSummary);

  const result = await requester.post(`${BASE_URL}/versions`,formData,{
    header: {
      "Content-Type": "multipart/form-data",
    },
  });
  return result;
}

export const getReview = async (documentVersionId,comment,reviewDecision) => {
  const result = requester.post(`${BASE_URL}/reviews`,{documentVersionId,comment,reviewDecision});
  return result;
}

export const getAllVersions = async (documentId) => {
  const result = requester.get(`${BASE_URL}/versions/document/${documentId}`);
  return result;
}


// --------------------------COMENTS----------------------

export const createComment = async (documentVersionId,body) => {
  const result = requester.post(`${BASE_URL}/comments`,{documentVersionId,body});
  return result;
}

export const getAllComments = async (versionId) => {
  const result = requester.get(`${BASE_URL}/comments/version/${versionId}`);
  return result;
}