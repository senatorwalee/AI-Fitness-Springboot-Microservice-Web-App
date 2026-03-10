import {
  BrowserRouter as Router,
  Navigate,
  Route,
  Routes,
  useLocation,
} from "react-router-dom";
import { Button } from "@mui/material";
import Box from "@mui/material/Box";
import { useContext, useEffect, useState } from "react";
import { AuthContext } from "react-oauth2-code-pkce";
import { useDispatch } from "react-redux";
import { setCredentials } from "./store/authSlice";
import ActivityForm from "./components/ActivityForm";
import ActivityList from "./components/ActivityList";
import ActivityDetail from "./components/ActivityDetail";

const ActivitiesPage = () => {
  const [refreshKey, setRefreshKey] = useState(0);

  return (
    <Box component="section" sx={{ p: 2, border: '1px dashed grey' }}>
      <ActivityForm onActivitiesAdded={() => setRefreshKey(k => k + 1)} />
      <ActivityList refreshKey={refreshKey} />
    </Box>
  );
};

// Root application component. Responsible for wiring authentication state
// from the OAuth library into the Redux store so the rest of the app can
// access the current user and token via `state.auth`.
function App() {
  // `AuthContext` is provided by `react-oauth2-code-pkce` and exposes the
  // current token, parsed token data, and helpers to log in/out.
  const { token, tokenData, logIn, logOut, isAuthenticated } =
    useContext(AuthContext);

  // Local UI state to indicate we've synchronized auth information into Redux
  // (e.g. so downstream components can wait for auth to be ready).
  const [authReady, setAuthReady] = useState(false);

  const dispatch = useDispatch();

  // Whenever a token becomes available or changes, write relevant pieces
  // of authentication info into the Redux `auth` slice. `tokenData` usually
  // contains the decoded ID token claims (user profile information).
  useEffect(() => {
    if (token) {
      // setCredentials expects an object like { token, user }
      dispatch(setCredentials({ token, user: tokenData }));
      setAuthReady(true);
    }
  }, [token, tokenData, dispatch]);

  // show protected screens based on `isAuthenticated` or `authReady`.
  return (
    <Router>
      {!token ? (
        <Button variant="contained" onClick={() => logIn()}>
          Login
        </Button>
      ) : (
        // <div>
        // <pre>{JSON.stringify(tokenData, null,2)}</pre>
        // </div>
        <Box component="section" sx={{ p: 2, border: "1px dashed grey" }}>
          <Routes>
          <Route path="/activities" element={<ActivitiesPage />} />
          <Route path="/activities/:id" element={<ActivityDetail />} />
          <Route path="/" element={token? <Navigate to="/activities" replace /> : <div>Welcome Please login.</div>} />
          </Routes>
        </Box>
      )}
    </Router>
  );
}

export default App;
