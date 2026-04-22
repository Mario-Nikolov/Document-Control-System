import { useCallback, useEffect, useState } from "react";
import {
  changeUserRole,
  createComment,
  createDocument,
  createNewVersion,
  deleteDocument,
  getAllComments,
  getAllDocuments,
  getAllUsers,
  getAllVersions,
  getLatesVersion,
  getOneUser,
  getReview,
  getUserHistory,
  login,
} from "../api/auth-api";
import { useAuthContext } from "../context/AuthContext";

export const useLogin = () => {
  const { changeAuthState } = useAuthContext();

  const loginHendler = async (email, password) => {
    const { password: _, ...authData } = await login(email, password);

    changeAuthState(authData);
    return authData;
  };

  return loginHendler;
};

// ---------------------USERS---------------------

export function useGetAllUsers() {
  const [users, setUsers] = useState([]);

  const fetchUsers = useCallback(async () => {
    const result = await getAllUsers();
     setUsers(result);
  },[]);

  useEffect(() => {
    fetchUsers();
  },[fetchUsers]);
  

  return [users, setUsers,fetchUsers];
}

export function useGetOneUser(userId) {
  const [user, setUser] = useState();

  useEffect(() => {
    (async () => {
      const result = await getOneUser(userId);
      setUser(result);
    })();
  }, [userId]);

  return [user, setUser];
}

export function useChangeUserRole() {
  const changeRoleHandler = async (userId, roleName) => {
    try {
      const response = await changeUserRole(userId, roleName);
      console.log(response.message);
      return response;
    } catch (err) {
      console.error("Грешка при смяна на пароата:", err);
      throw err;
    }
  };
  return { changeRoleHandler };
}

export function useGetUserHistory(){
  const [logs,setLogs] = useState([]);

  const fetchUsersHistory = useCallback(async () => {
    const result = await getUserHistory();
    setLogs(result);
  },[])

  useEffect(() =>{
    fetchUsersHistory();
  },[fetchUsersHistory])

  return[logs,fetchUsersHistory];
}

// ---------------------DOC----------------------------

export function useCreateDocument() {
  const createDocumentHandler = async (values) => {
    try {
       console.log("Изпращам:", values);
      const result = await createDocument(values);
      return result;
    } catch (err) {
      console.error("Грешка при създаване на документ", err);
      throw err;
    }
  };

  return { createDocumentHandler };
}

export function useGetAllDocuments() {
  const [documents, setDocuments] = useState([]);
  const fetchDocuments = useCallback(async () => {
    const result = await getAllDocuments();
    setDocuments(result);
  },[]);

  useEffect(() => {
    fetchDocuments();
  },[fetchDocuments])

  return [documents, ,fetchDocuments];
}

export function useGetLatestVersionDoc(documentId) {
  const [document, setDocument] = useState();

  const fetchDocument = useCallback(async () => {
    const result = await getLatesVersion(documentId);
    setDocument(result);
  }, [documentId]);

  useEffect(() => {
    fetchDocument();
  }, [fetchDocument]);

  return [document, setDocument, fetchDocument];
}

// -------------------------VERSIONS---------------------------

export function useCreateNewVersion() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const createVersionHandler = async (
    documentId,
    file,
    extension,
    changeSummary,
  ) => {
    setLoading(true);
    setError(null);

    try {
      const result = await createNewVersion(
        documentId,
        file,
        extension,
        changeSummary,
      );
      return result;
    } catch (err) {
      console.error("Грешка при създаване на документ", err);
      throw err;
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return { createVersionHandler, loading, error };
}

export function useGetReview() {
  const getReviewHandler = async (
    documentVersionId,
    comment,
    reviewDecision,
  ) => {
    try {
      const result = await getReview(
        documentVersionId,
        comment,
        reviewDecision,
      );
      return result;
    } catch (err) {
      console.error("Грешка при одобряването/отхвърлянето на версията", err);
      throw err;
    }
  };

  return { getReviewHandler };
}

export function useGetAllVersions(documentId) {
  const [reviews, setReviews] = useState([]);

  const fetchVersions = useCallback(async () => {
    const result = await getAllVersions(documentId);
    setReviews(result);
  }, [documentId]);

  useEffect(() => {
    fetchVersions();
  }, [fetchVersions]);

  return [reviews, setReviews, fetchVersions];
}

// -----------------------------COMMENT-------------------------

export function useCreateComment() {
  const createCommentHandler = async (documentVersionId, body) => {
    try {
      const result = await createComment(documentVersionId, body);
      return result;
    } catch (err) {
      console.error("Грешка при създаване на коментар:", err);
      throw err;
    }
  };
  return { createCommentHandler };
}

export function useGetAllComments(versionId) {
  const [comments, setComments] = useState([]);

  const fetchComments = useCallback(async () => {
    if (!versionId) return;

    const result = await getAllComments(versionId);
    setComments(result);
  }, [versionId]);

  useEffect(() => {
    fetchComments();
  }, [fetchComments]);

  return { comments, refreshComments: fetchComments };
}
