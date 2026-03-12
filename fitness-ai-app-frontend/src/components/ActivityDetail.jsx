import React, { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import {
  Box, Typography, CircularProgress, Card, CardContent,
  Divider, Chip, List, ListItem, ListItemIcon, ListItemText,
  Button, Stack, Avatar,
} from '@mui/material'
import ArrowBackIcon from '@mui/icons-material/ArrowBack'
import LightbulbIcon from '@mui/icons-material/Lightbulb'
import TrendingUpIcon from '@mui/icons-material/TrendingUp'
import FitnessCenterIcon from '@mui/icons-material/FitnessCenter'
import WarningAmberIcon from '@mui/icons-material/WarningAmber'
import TimerIcon from '@mui/icons-material/Timer'
import LocalFireDepartmentIcon from '@mui/icons-material/LocalFireDepartment'
import CheckCircleOutlineIcon from '@mui/icons-material/CheckCircleOutline'
import { getActivityDetail, getActivity } from '../services/api'

const ActivityDetail = () => {
  const { id } = useParams()
  const navigate = useNavigate()
  const [data, setData] = useState(null)
  const [activity, setActivity] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    let mounted = true
    const load = async () => {
      try {
        const [recommendation, activityData] = await Promise.all([
          getActivityDetail(id),
          getActivity(id),
        ])
        if (mounted) {
          setData(recommendation)
          setActivity(activityData)
        }
      } catch (err) {
        console.error(err)
        if (mounted) setError(err)
      } finally {
        if (mounted) setLoading(false)
      }
    }
    load()
    return () => { mounted = false }
  }, [id])

  if (loading) return (
    <Box display="flex" justifyContent="center" my={8}><CircularProgress /></Box>
  )

  if (error) return (
    <Box p={3} maxWidth={800} mx="auto">
      <Typography color="error" gutterBottom>Could not load activity details.</Typography>
      <Button startIcon={<ArrowBackIcon />} onClick={() => navigate('/activities')} sx={{ mt: 1 }}>
        Back to Activities
      </Button>
    </Box>
  )

  if (!data) return null

  /* Helper: render a numbered list with check-circle icons */
  const renderList = (items) => (
    <List dense disablePadding>
      {(items || []).map((item, i) => (
        <ListItem key={i} sx={{ pl: 0, alignItems: 'flex-start' }}>
          <ListItemIcon sx={{ minWidth: 32, mt: 0.5, color: 'primary.main' }}>
            <CheckCircleOutlineIcon fontSize="small" />
          </ListItemIcon>
          <ListItemText primary={item} />
        </ListItem>
      ))}
    </List>
  )

  /* Reusable section card */
  const Section = ({ icon, title, color = 'primary.main', children }) => (
    <Card sx={{ mb: 2.5 }}>
      <CardContent sx={{ p: 3 }}>
        <Stack direction="row" alignItems="center" spacing={1.5} sx={{ mb: 2 }}>
          <Avatar sx={{ bgcolor: color, width: 36, height: 36 }}>{icon}</Avatar>
          <Typography variant="h6">{title}</Typography>
        </Stack>
        <Divider sx={{ mb: 2 }} />
        {children}
      </CardContent>
    </Card>
  )

  return (
    <Box maxWidth={800} mx="auto" py={2}>
      {/* Back button */}
      <Button
        startIcon={<ArrowBackIcon />}
        onClick={() => navigate('/activities')}
        sx={{ mb: 3 }}
      >
        Back to Activities
      </Button>

      {/* Activity summary */}
      <Section
        icon={<FitnessCenterIcon fontSize="small" />}
        title="Activity Summary"
      >
        <Stack direction="row" gap={1.5} flexWrap="wrap">
          <Chip
            label={data.activityType || activity?.type || '-'}
            color="primary"
            variant="filled"
          />
          <Chip
            icon={<TimerIcon />}
            label={`${activity?.duration ?? '-'} min`}
            variant="outlined"
          />
          <Chip
            icon={<LocalFireDepartmentIcon />}
            label={`${activity?.caloriesBurned ?? '-'} cal`}
            variant="outlined"
          />
        </Stack>
      </Section>

      {/* Recommendation */}
      <Section
        icon={<LightbulbIcon fontSize="small" />}
        title="Recommendation"
        color="#f9a825"
      >
        <Typography variant="body1" sx={{ lineHeight: 1.8 }}>
          {data.recommendation || '-'}
        </Typography>
      </Section>

      {/* Improvements */}
      {data.improvements?.length > 0 && (
        <Section
          icon={<TrendingUpIcon fontSize="small" />}
          title="Improvements"
          color="#66bb6a"
        >
          {renderList(data.improvements)}
        </Section>
      )}

      {/* Suggestions */}
      {data.suggestions?.length > 0 && (
        <Section
          icon={<FitnessCenterIcon fontSize="small" />}
          title="Suggestions"
          color="#4d90fe"
        >
          {renderList(data.suggestions)}
        </Section>
      )}

      {/* Safety */}
      {data.safety?.length > 0 && (
        <Section
          icon={<WarningAmberIcon fontSize="small" />}
          title="Safety"
          color="#ef5350"
        >
          {renderList(data.safety)}
        </Section>
      )}
    </Box>
  )
}

export default ActivityDetail