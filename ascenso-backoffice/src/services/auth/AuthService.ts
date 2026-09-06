import { apiClient } from '@/services/http/ApiClient'
import { API_ENDPOINTS } from '@/config/endpoints'
import type { LoginResponse, MeResponse } from '@/types/auth'
import type { Destacamento } from '@/types/destacamentos'

class AuthService {
  login(username: string, password: string) {
    return apiClient.post<LoginResponse>(
      API_ENDPOINTS.auth.login,
      { username, password },
      { requiresAuth: false },
    )
  }

  me() {
    return apiClient.get<MeResponse>(API_ENDPOINTS.auth.me)
  }

  misDestacamentos() {
    return apiClient.get<Destacamento[]>(API_ENDPOINTS.auth.misDestacamentos)
  }

  logout() {
    return apiClient.post<void>(API_ENDPOINTS.auth.logout, undefined, { requiresAuth: false })
  }

  forgotPassword(username: string) {
    return apiClient.post<void>(API_ENDPOINTS.auth.forgotPassword, { username }, { requiresAuth: false })
  }

  resetPassword(token: string, nuevaPassword: string) {
    return apiClient.post<void>(
      API_ENDPOINTS.auth.resetPassword,
      { token, nuevaPassword },
      { requiresAuth: false },
    )
  }
}

export const authService = new AuthService()
