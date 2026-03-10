import React, { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import {
  Box, Typography, CircularProgress, Card, CardContent,
  Divider, Chip, List, ListItem, ListItemText, Button
} from '@mui/material'
import { getActivityDetail } from '../services/api'

const ActivityDetail = () => {
  const { id } = useParams()
  const navigate = useNavigate()
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    let mounted = true
    const load = async () => {
      try {
        const result = await getActivityDetail(id)
        if (mounted) setData(result)
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
    <Box display="flex" justifyContent="center" my={6}><CircularProgress /></Box>
  )

  if (error) return (
    <Box p={3}>
      <Typography color="error">Could not load activity details.</Typography>
      <Button onClick={() => navigate('/activities')} sx={{ mt: 2 }}>← Back</Button>
    </Box>
  )

  if (!data) return null

  const renderList = (items) => (
    <List dense disablePadding>
      {(items || []).map((item, i) => (
        <ListItem key={i} sx={{ pl: 0 }}>
          <ListItemText primary={`${i + 1}. ${item}`} />
        </ListItem>
      ))}
    </List>
  )

  return (
    <Box p={3} maxWidth={800} mx="auto">
      <Button onClick={() => navigate('/activities')} sx={{ mb: 2 }}>← Back to Activities</Button>

      {/* Activity summary */}
      <Card sx={{ mb: 3 }}>
        <CardContent>
          <Typography variant="h5" gutterBottom>Activity Summary</Typography>
          <Divider sx={{ mb: 2 }} />
          <Box display="flex" gap={2} flexWrap="wrap">
            <Chip label={`Type: ${data.activityType || '-'}`} color="primary" />
            <Chip label={`Duration: ${data.duration ?? '-'} min`} />
            <Chip label={`Calories: ${data.caloriesBurned ?? '-'}`} />
          </Box>
        </CardContent>
      </Card>

      {/* Recommendation */}
      <Card sx={{ mb: 3 }}>
        <CardContent>
          <Typography variant="h6" gutterBottom>💡 Recommendation</Typography>
          <Divider sx={{ mb: 1 }} />
          <Typography variant="body1">{data.recommendation || '-'}</Typography>
        </CardContent>
      </Card>

      {/* Improvements */}
      {data.improvements?.length > 0 && (
        <Card sx={{ mb: 3 }}>
          <CardContent>
            <Typography variant="h6" gutterBottom>📈 Improvements</Typography>
            <Divider sx={{ mb: 1 }} />
            {renderList(data.improvements)}
          </CardContent>
        </Card>
      )}

      {/* Suggestions */}
      {data.suggestions?.length > 0 && (
        <Card sx={{ mb: 3 }}>
          <CardContent>
            <Typography variant="h6" gutterBottom>🏋️ Suggestions</Typography>
            <Divider sx={{ mb: 1 }} />
            {renderList(data.suggestions)}
          </CardContent>
        </Card>
      )}

      {/* Safety */}
      {data.safety?.length > 0 && (
        <Card sx={{ mb: 3 }}>
          <CardContent>
            <Typography variant="h6" gutterBottom>⚠️ Safety</Typography>
            <Divider sx={{ mb: 1 }} />
            {renderList(data.safety)}
          </CardContent>
        </Card>
      )}
    </Box>
  )
}

export default ActivityDetail