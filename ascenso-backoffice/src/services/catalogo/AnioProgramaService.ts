import { apiClient } from '@/services/http/ApiClient'
import { API_ENDPOINTS } from '@/config/endpoints'
import type { AnioPrograma, GuardarAnioProgramaRequest } from '@/types/catalogo'

class AnioProgramaService {
  listar(grupoId?: number) {
    return apiClient.get<AnioPrograma[]>(API_ENDPOINTS.catalogo.aniosPrograma, {
      params: grupoId ? { grupoId } : undefined,
    })
  }

  crear(request: GuardarAnioProgramaRequest) {
    return apiClient.post<AnioPrograma>(API_ENDPOINTS.catalogo.aniosPrograma, request)
  }

  actualizar(id: number, request: GuardarAnioProgramaRequest) {
    return apiClient.put<AnioPrograma>(API_ENDPOINTS.catalogo.anioProgramaPorId(id), request)
  }
}

export const anioProgramaService = new AnioProgramaService()
