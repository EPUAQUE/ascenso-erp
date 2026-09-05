import { apiClient } from '@/services/http/ApiClient'
import { API_ENDPOINTS } from '@/config/endpoints'
import type { Actividad, GuardarActividadRequest } from '@/types/actividades'

class ActividadService {
  listar(destacamentoId: number) {
    return apiClient.get<Actividad[]>(API_ENDPOINTS.actividades.porDestacamento(destacamentoId))
  }

  crear(destacamentoId: number, request: GuardarActividadRequest) {
    return apiClient.post<Actividad>(API_ENDPOINTS.actividades.porDestacamento(destacamentoId), request)
  }

  actualizar(destacamentoId: number, id: number, request: GuardarActividadRequest) {
    return apiClient.put<Actividad>(API_ENDPOINTS.actividades.porId(destacamentoId, id), request)
  }
}

export const actividadService = new ActividadService()
