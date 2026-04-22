import { createContext, useContext } from "react";
import usePersistedState from "../hooks/usePersistedState";

export const AuthContext = createContext({
  userId: "",
  userName: "",
  email: "",
  roleName: "",
  accessToken: "",
  isAuthenticated: false,
  changeAuthState: (authState = {}) => null,
  logout: () => null,
});

export function AuthContextProvider(props) {
  const [authState, setAuthState] = usePersistedState("auth", {});

  const changeAuthState = (state) => {
    setAuthState(state);
  };

  const logout = () => {
    setAuthState(null);
  };

  const contextData = {
    userId: authState?.userId || authState?.auth?.userId,
    userName: authState?.username || authState?.auth?.username,
    email: authState?.email || authState?.auth?.email,
    roleName: authState?.roleName || authState?.auth?.roleName,
    accessToken: authState?.accessToken || authState?.auth?.accessToken,
    isAuthenticated: !!(authState?.accessToken || authState?.auth?.accessToken),
    changeAuthState,
    logout,
  };

  return (
    <AuthContext.Provider value={contextData}>
      {props.children}
    </AuthContext.Provider>
  );
}

export function useAuthContext() {
  const authData = useContext(AuthContext);
  return authData;
}
