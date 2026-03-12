import React, { useState } from 'react'
import {
  Box, Card, CardContent, FormControl, InputLabel,
  MenuItem, Select, TextField, Button, Typography, Stack,
} from '@mui/material'
import AddCircleOutlineIcon from '@mui/icons-material/AddCircleOutline'
import { addActivity } from '../services/api'

const ActivityForm = ({ onActivityAdded, onActivitiesAdded }) => {
  const [activity, setActivity] = useState({
    type: 'RUNNING',
    duration: '',
    caloriesBurned: '',
    additionalMetrics: {},
  })

  const handleSubmit = async (e) => {
    e.preventDefault()
    try {
      const saved = await addActivity(activity)
      const onAdded = onActivityAdded || onActivitiesAdded
      if (onAdded) onAdded(saved)
      setActivity({ type: 'RUNNING', duration: '', caloriesBurned: '', additionalMetrics: {} })
    } catch (error) {
      console.log(error)
    }
  }

  return (
    <Card sx={{ mb: 4 }}>
      <CardContent sx={{ p: 3 }}>
        <Typography variant="h6" sx={{ mb: 2.5, display: 'flex', alignItems: 'center', gap: 1 }}>
          <AddCircleOutlineIcon color="primary" />
          Log New Activity
        </Typography>

        <Box component="form" onSubmit={handleSubmit}>
          <Stack spacing={2.5}>
            <FormControl fullWidth>
              <InputLabel>Activity Type</InputLabel>
              <Select
                value={activity.type}
                label="Activity Type"
                onChange={(e) => setActivity({ ...activity, type: e.target.value })}
              >
                <MenuItem value="RUNNING">🏃 Running</MenuItem>
                <MenuItem value="WALKING">🚶 Walking</MenuItem>
                <MenuItem value="CYCLING">🚴 Cycling</MenuItem>
                <MenuItem value="SWIMMING">🏊 Swimming</MenuItem>
                <MenuItem value="BOXING">🥊 Boxing</MenuItem>
              </Select>
            </FormControl>

            <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
              <TextField
                fullWidth
                label="Duration (minutes)"
                type="number"
                inputProps={{ min: 0 }}
                value={activity.duration}
                onChange={(e) => setActivity({ ...activity, duration: e.target.value })}
              />
              <TextField
                fullWidth
                label="Calories Burned"
                type="number"
                inputProps={{ min: 0 }}
                value={activity.caloriesBurned}
                onChange={(e) => setActivity({ ...activity, caloriesBurned: e.target.value })}
              />
            </Stack>

            <Button
              type="submit"
              variant="contained"
              size="large"
              fullWidth
              startIcon={<AddCircleOutlineIcon />}
            >
              Add Activity
            </Button>
          </Stack>
        </Box>
      </CardContent>
    </Card>
  )
}

export default ActivityForm