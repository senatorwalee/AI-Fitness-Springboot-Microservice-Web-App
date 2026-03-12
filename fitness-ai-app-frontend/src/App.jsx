import {
  BrowserRouter as Router,
  Navigate,
  Route,
  Routes,
} from "react-router-dom";
import {
  AppBar,
  Toolbar,
  Typography,
  Button,
  Container,
  Box,
  Card,
  CardContent,
  Avatar,
} from "@mui/material";
import FitnessCenterIcon from "@mui/icons-material/FitnessCenter";
import LogoutIcon from "@mui/icons-material/Logout";
import LoginIcon from "@mui/icons-material/Login";
import { useContext, useEffect, useState } from "react";
import { AuthContext } from "react-oauth2-code-pkce";
import { useDispatch } from "react-redux";
import { setCredentials } from "./store/authSlice";
import ActivityForm from "./components/ActivityForm";
import ActivityList from "./components/ActivityList";
import ActivityDetail from "./components/ActivityDetail";

/* ── Activities dashboard (form + list) ─────────────────────── */
const ActivitiesPage = () => {
  const [refreshKey, setRefreshKey] = useState(0);

  return (
    <Box sx={{ mt: 3 }}>
      <ActivityForm onActivitiesAdded={() => setRefreshKey((k) => k + 1)} />
      <ActivityList refreshKey={refreshKey} />
    </Box>
  );
};

/* ── Root App component ─────────────────────────────────────── */
function App() {
  const { token, tokenData, logIn, logOut } =
    useContext(AuthContext);
  const dispatch = useDispatch();

  useEffect(() => {
    if (token) {
      dispatch(setCredentials({ token, user: tokenData }));
    }
  }, [token, tokenData, dispatch]);

  /* ── Unauthenticated: centered login card ───────────────── */
  if (!token) {
    return (
      <Box
        sx={{
          minHeight: "100vh",
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
        }}
      >
        <Card
          sx={{
            maxWidth: 420,
            width: "100%",
            mx: 2,
            textAlign: "center",
            py: 4,
            px: 3,
          }}
        >
          <CardContent>
            <Avatar
              sx={{
                bgcolor: "primary.main",
                width: 64,
                height: 64,
                mx: "auto",
                mb: 2,
              }}
            >
              <FitnessCenterIcon sx={{ fontSize: 36 }} />
            </Avatar>
            <Typography variant="h4" gutterBottom>
              AI Fitness
            </Typography>
            <Typography variant="body2" color="text.secondary" sx={{ mb: 4 }}>
              Track your workouts and get personalized AI recommendations
            </Typography>
            <Button
              variant="contained"
              size="large"
              fullWidth
              startIcon={<LoginIcon />}
              onClick={() => logIn()}
            >
              Sign In
            </Button>
          </CardContent>
        </Card>
      </Box>
    );
  }

  /* ── Authenticated: AppBar + routed content ─────────────── */
  return (
    <Router>
      <AppBar position="sticky" elevation={0}>
        <Toolbar>
          <FitnessCenterIcon sx={{ mr: 1.5 }} />
          <Typography variant="h6" sx={{ flexGrow: 1, fontWeight: 700 }}>
            AI Fitness
          </Typography>
          <Button
            color="inherit"
            startIcon={<LogoutIcon />}
            onClick={() => logOut()}
            sx={{ textTransform: "none" }}
          >
            Logout
          </Button>
        </Toolbar>
      </AppBar>

      <Container maxWidth="lg" sx={{ py: 3 }}>
        <Routes>
          <Route path="/activities" element={<ActivitiesPage />} />
          <Route path="/activities/:id" element={<ActivityDetail />} />
          <Route path="/" element={<Navigate to="/activities" replace />} />
        </Routes>
      </Container>
    </Router>
  );
}

export default App;
