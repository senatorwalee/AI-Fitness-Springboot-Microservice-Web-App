import axios from "axios";

const API_BASE_URL = "http://localhost:8085/api";

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

api.interceptors.request.use((config) => {
  const userId = localStorage.getItem("userId");
  const token = localStorage.getItem("token");
  if(userId){
    config.headers['X-User-ID'] = userId;
  }
  if(token){
    config.headers['Authorization'] = `Bearer ${token}`;
  }
  // Must return the config so axios can continue the request chain.
  return config;
}, (error) => Promise.reject(error));

export const getUserActivities = async (userId) => {
  try {
    const response = await api.get(`/activities/user/${userId}`);
    return response.data;
  } catch (error) {
    console.error("Error fetching activities:", error);
    throw error;
  }
};

export const addActivity = async (activity) => {
  try {
    const response = await api.post("/activities", activity);
    return response.data;
  } catch (error) {
    console.error("Error adding activity:", error);
    throw error;
  }
};

export const getActivityDetail = async (activityId) => {
  try {
    const response = await api.get(`/ai/recommendations/activity/${activityId}`);
    return response.data;
  } catch (error) {
    console.error("Error fetching activity detail:", error);
    throw error;
  }
};