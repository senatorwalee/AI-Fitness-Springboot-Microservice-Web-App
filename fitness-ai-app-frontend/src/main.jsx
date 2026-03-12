import React from 'react'
import ReactDOM from 'react-dom/client'
import {AuthProvider} from 'react-oauth2-code-pkce'
import { ThemeProvider, CssBaseline } from '@mui/material'
import { Provider } from 'react-redux'
import { store } from './store/store'
import theme from './theme'
import App from './App'
import { authConfig } from './authConfig'
import './index.css'

const root = ReactDOM.createRoot(document.getElementById('root'))
root.render(
  <AuthProvider authConfig={authConfig}
                    loadingComponent={<div>Loading</div>}>
    <Provider store={store}>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <App />
      </ThemeProvider>
    </Provider>
  </AuthProvider>,
)