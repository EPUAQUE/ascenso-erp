import { apiClient } from '@/services/http/ApiClient'
import { API_ENDPOINTS } from '@/config/endpoints'
import type { ActualizarGrupoRequest, Grupo } from '@/types/catalogo'

class GrupoService {
  listar() {
    return apiClient.get<Grupo[]>(API_ENDPOINTS.catalogo.grupos)
  }

  obtener(id: number) {
    return apiClient.get<Grupo>(API_ENDPOINTS.catalogo.grupoPorId(id))
  }

  actualizar(id: number, request: ActualizarGrupoRequest) {
    return apiClient.put<Grupo>(API_ENDPOINTS.catalogo.grupoPorId(id), request)
  }
}

export const grupoService = new GrupoService()
