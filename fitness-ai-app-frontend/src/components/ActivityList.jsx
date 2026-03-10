import React, { useEffect, useState } from 'react'
import { Grid, Card, CardContent, Typography, CircularProgress, Box, CardActionArea } from '@mui/material'
import { useNavigate } from 'react-router-dom'
import { getUserActivities } from '../services/api';

const ActivityList = ({ refreshKey }) => {
  const [activities, setActivities] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const navigate = useNavigate()

  const userId = localStorage.getItem('userId')

  useEffect(() => {
    let mounted = true
    const load = async () => {
      setLoading(true)
      try {
        const data = await getUserActivities(userId)
        if (mounted) setActivities(data || [])
      } catch (err) {
        console.error(err)
        if (mounted) setError(err)
      } finally {
        if (mounted) setLoading(false)
      }
    }
    if (userId) load()
    else setLoading(false)
    return () => { mounted = false }
  }, [userId, refreshKey])

  if (loading) return (
    <Box display="flex" justifyContent="center" my={4}>
      <CircularProgress />
    </Box>
  )

  if (error) return (
    <Typography color="error">Error loading activities.</Typography>
  )

  if (!activities.length) return (
    <Typography>No activities yet. Add one to get started.</Typography>
  )

  return (
    <Grid container spacing={2}>
      {activities.map((act) => (
        <Grid item key={act.id || act._id || JSON.stringify(act)} xs={12} sm={6} md={4}>
          <Card sx={{ cursor: 'pointer' }}>
            <CardActionArea onClick={() => navigate(`/activities/${act.id || act._id}`)}>
              <CardContent>
                <Typography variant="h6" gutterBottom>{act.type || 'Activity'}</Typography>
                <Typography variant="body2">Duration: {act.duration ?? act.minutes ?? '-'} minutes</Typography>
                <Typography variant="body2">Calories: {act.caloriesBurned ?? act.calories ?? '-'}</Typography>
                {act.notes && <Typography variant="body2">Notes: {act.notes}</Typography>}
                <Typography variant="caption" color="textSecondary">{act.createdAt ? new Date(act.createdAt).toLocaleString() : ''}</Typography>
              </CardContent>
            </CardActionArea>
          </Card>
        </Grid>
      ))}
    </Grid>
  )
}

export default ActivityList