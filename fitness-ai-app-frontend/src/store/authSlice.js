import { createSlice } from "@reduxjs/toolkit";

// Slice handling authentication state (user, token, userId).
// Persists some parts to localStorage for simple session persistence.
const authSlice = createSlice({
  name: "auth",
  // Initialize state from localStorage when available.
  initialState: {
    // `user` is stored as a JSON string; parse it back to an object.
    user: JSON.parse(localStorage.getItem("user")) || null,
    // `token` is a raw JWT string (not JSON). Do not JSON.parse it.
    token: localStorage.getItem("token") || null,
    // `userId` stored as a plain string.
    userId: localStorage.getItem("userId") || null,
  },
  reducers: {
    // setCredentials updates auth state and persists token/user to localStorage
    // Expected payload shape: { user, token, sub }
    setCredentials: (state, action) => {
      state.user = action.payload.user;
      state.token = action.payload.token;

      // Derive userId from possible locations in the payload.
      const derivedUserId = action.payload.sub || action.payload.user?.sub || action.payload.user?.userId || action.payload.user?.id || null;
      state.userId = derivedUserId;

      // persist token (raw string) and user (JSON) to localStorage
      if (action.payload.token) localStorage.setItem("token", action.payload.token);
      if (action.payload.user) localStorage.setItem("user", JSON.stringify(action.payload.user));
      if (derivedUserId) localStorage.setItem("userId", derivedUserId);
    },
    // logout clears auth state in memory.
    // Note: current implementation reads localStorage keys (no removal).
    logout: (state) => {
      // Clear in-memory state
      state.user = null;
      state.token = null;
      state.userId = null;

      // Remove persisted auth from localStorage
      localStorage.removeItem("token");
      localStorage.removeItem("user");
      localStorage.removeItem("userId");
    },
  },
});

// Named exports for action creators
export const { setCredentials, logout } = authSlice.actions;
// Default export is the slice's reducer function (used by configureStore)
// `authSlice.reducer` is singular — it is the reducer function created by createSlice.
export default authSlice.reducer;
