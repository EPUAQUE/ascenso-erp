import { apiClient, ApiClientError } from '@/services/http/ApiClient'
import { API_ENDPOINTS } from '@/config/endpoints'
import type { LogroMayor, OtorgarLogroMayorRequest } from '@/types/ninos'

class LogroMayorService {
  /** null cuando el niño todavía no lo recibe — el backend responde 404 en ese caso. */
  async obtener(destacamentoId: number, ninoId: number): Promise<LogroMayor | null> {
    try {
      return await apiClient.get<LogroMayor>(API_ENDPOINTS.logroMayor.porNino(destacamentoId, ninoId))
    } catch (error) {
      if (error instanceof ApiClientError && error.status === 404) return null
      throw error
    }
  }

  otorgar(destacamentoId: number, ninoId: number, request: OtorgarLogroMayorRequest) {
    return apiClient.post<LogroMayor>(API_ENDPOINTS.logroMayor.porNino(destacamentoId, ninoId), request)
  }
}

export const logroMayorService = new LogroMayorService()
