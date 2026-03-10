
// Configuration object for the OAuth2 PKCE client used by the app.
// Kept in a separate file so it can be imported anywhere authentication
// setup is required (e.g. provider initialization or login helpers).
export const authConfig = {
  // OAuth2 client identifier registered with the auth server.
  clientId: 'oauth2-pkce-client',

  // Authorization endpoint where the user is redirected to sign in.
  authorizationEndpoint:
    'http://localhost:8086/realms/fitness-oauth2/protocol/openid-connect/auth',

  // Token endpoint used to exchange the authorization code for tokens.
  tokenEndpoint:
    'http://localhost:8086/realms/fitness-oauth2/protocol/openid-connect/token',

  // Redirect URI registered for the client. The auth server will redirect
  // back here after the user completes sign-in.
  redirectUri: 'http://localhost:5173',

  // Scopes requested from the provider. `offline_access` allows refresh tokens.
  scope: 'openid profile email offline_access',

  // Handler invoked when a refresh token expires. Default behavior here
  // attempts to start a fresh login flow by calling `event.logIn()` on
  // the library-provided event object.
  onRefreshTokenExpire: (event) => event.logIn(),
};
