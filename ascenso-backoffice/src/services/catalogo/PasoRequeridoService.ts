import { apiClient } from '@/services/http/ApiClient'
import { API_ENDPOINTS } from '@/config/endpoints'
import type { GuardarPasoRequeridoRequest, PasoRequerido } from '@/types/catalogo'

class PasoRequeridoService {
  listar(anioProgramaId?: number) {
    return apiClient.get<PasoRequerido[]>(API_ENDPOINTS.catalogo.pasosRequeridos, {
      params: anioProgramaId ? { anioProgramaId } : undefined,
    })
  }

  crear(request: GuardarPasoRequeridoRequest) {
    return apiClient.post<PasoRequerido>(API_ENDPOINTS.catalogo.pasosRequeridos, request)
  }

  actualizar(id: number, request: GuardarPasoRequeridoRequest) {
    return apiClient.put<PasoRequerido>(API_ENDPOINTS.catalogo.pasoRequeridoPorId(id), request)
  }
}

export const pasoRequeridoService = new PasoRequeridoService()
