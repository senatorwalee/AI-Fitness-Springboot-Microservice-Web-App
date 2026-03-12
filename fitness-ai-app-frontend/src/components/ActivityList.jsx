import React, { useEffect, useState } from 'react'
import {
  Grid, Card, CardContent, Typography, CircularProgress,
  Box, CardActionArea, Chip, Stack,
} from '@mui/material'
import DirectionsRunIcon from '@mui/icons-material/DirectionsRun'
import DirectionsWalkIcon from '@mui/icons-material/DirectionsWalk'
import DirectionsBikeIcon from '@mui/icons-material/DirectionsBike'
import PoolIcon from '@mui/icons-material/Pool'
import SportsMmaIcon from '@mui/icons-material/SportsMma'
import TimerIcon from '@mui/icons-material/Timer'
import LocalFireDepartmentIcon from '@mui/icons-material/LocalFireDepartment'
import { useNavigate } from 'react-router-dom'
import { getUserActivities } from '../services/api'

/* Map activity type → MUI icon */
const activityIcons = {
  RUNNING: <DirectionsRunIcon />,
  WALKING: <DirectionsWalkIcon />,
  CYCLING: <DirectionsBikeIcon />,
  SWIMMING: <PoolIcon />,
  BOXING: <SportsMmaIcon />,
}

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
    <Box display="flex" justifyContent="center" my={6}>
      <CircularProgress />
    </Box>
  )

  if (error) return (
    <Typography color="error" sx={{ mt: 3 }}>Error loading activities.</Typography>
  )

  if (!activities.length) return (
    <Box textAlign="center" sx={{ mt: 6, opacity: 0.7 }}>
      <DirectionsRunIcon sx={{ fontSize: 48, mb: 1, color: 'text.secondary' }} />
      <Typography color="text.secondary">
        No activities yet. Log one above to get started!
      </Typography>
    </Box>
  )

  return (
    <>
      <Typography variant="h6" sx={{ mb: 2 }}>
        Your Activities
      </Typography>

      <Grid container spacing={2.5}>
        {activities.map((act) => (
          <Grid item key={act.id || act._id || JSON.stringify(act)} xs={12} sm={6} md={4}>
            <Card>
              <CardActionArea onClick={() => navigate(`/activities/${act.id || act._id}`)}>
                <CardContent sx={{ p: 2.5 }}>
                  {/* Header row: icon + type */}
                  <Stack direction="row" alignItems="center" spacing={1} sx={{ mb: 1.5 }}>
                    <Box sx={{ color: 'primary.main', display: 'flex' }}>
                      {activityIcons[act.type] || <DirectionsRunIcon />}
                    </Box>
                    <Typography variant="subtitle1" fontWeight={600}>
                      {act.type || 'Activity'}
                    </Typography>
                  </Stack>

                  {/* Stats chips */}
                  <Stack direction="row" spacing={1} flexWrap="wrap" sx={{ mb: 1.5 }}>
                    <Chip
                      icon={<TimerIcon />}
                      label={`${act.duration ?? act.minutes ?? '-'} min`}
                      size="small"
                      variant="outlined"
                    />
                    <Chip
                      icon={<LocalFireDepartmentIcon />}
                      label={`${act.caloriesBurned ?? act.calories ?? '-'} cal`}
                      size="small"
                      variant="outlined"
                    />
                  </Stack>

                  {/* Timestamp */}
                  <Typography variant="caption" color="text.secondary">
                    {act.createdAt ? new Date(act.createdAt).toLocaleString() : ''}
                  </Typography>
                </CardContent>
              </CardActionArea>
            </Card>
          </Grid>
        ))}
      </Grid>
    </>
  )
}

export default ActivityList