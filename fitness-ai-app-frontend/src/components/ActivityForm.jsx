import React, { useState } from 'react'
import { Box, FormControl, InputLabel, MenuItem, Select, TextField, Button } from '@mui/material'
import { addActivity } from '../services/api';

// ActivityForm collects activity type, duration (minutes), and calories burned.
// It calls the provided callback when an activity is added. The prop name
// used by callers varies in the codebase (`onActivityAdded` vs `onActivitiesAdded`),
// so we accept both and prefer whichever is provided.
const ActivityForm = ({ onActivityAdded, onActivitiesAdded }) => {
  const [activity, setActivity] = useState({ type: "RUNNING", duration: '', caloriesBurned: '', additionalMetrics: {} });
  
  const hanndleSubmit = async (e) => {
    e.preventDefault();
    try {
      // Persist the activity and use server response
      const saved = await addActivity(activity);
      const onAdded = onActivityAdded || onActivitiesAdded;
      if (onAdded) onAdded(saved);
      
      // reset form
      setActivity({ type: "RUNNING", duration: '', caloriesBurned: '', additionalMetrics: {} })
      
    } catch (error) {
      console.log(error);
      
    }
  }
  return (
    
    <Box component="form" onSubmit={hanndleSubmit} sx={{ mb:4 }}>
      <FormControl fullWidth sx={{mb:2}}>
        <InputLabel>Activity Type</InputLabel>
        <Select value={activity.type} onChange={e=>setActivity({...activity, type: e.target.value})}>
          <MenuItem value ="RUNNING">Running</MenuItem>
          <MenuItem value ="WALKING">Walking</MenuItem>
          <MenuItem value ="CYCLING">Cycling</MenuItem>
          <MenuItem value ="SWIMMING">Swimming</MenuItem>
          <MenuItem value ="BOXING">Boxing</MenuItem>
        </Select>
      
      </FormControl>
      <TextField fullWidth label="Duration (minutes)" type="number" inputProps={{min:0}} sx={{mb:2}} value={activity.duration} onChange={e=>setActivity({...activity, duration: e.target.value})}/>
      <TextField fullWidth label="Calories burned" type="number" inputProps={{min:0}} sx={{mb:2}} value={activity.caloriesBurned} onChange={e=>setActivity({...activity, caloriesBurned: e.target.value})}/>
      <Button type="submit" variant="contained">Add Activity</Button>
    </Box>

    
  )
}

export default ActivityForm