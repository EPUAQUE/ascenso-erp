import { defineStore } from 'pinia'
import { destacamentoService } from '@/services/destacamentos/DestacamentoService'
import { usePermissionsStore } from './permissions.store'

interface DestacamentoOpcion {
  id: number
  nombre: string
}

const ACTUAL_KEY = 'exploradores-destacamento-actual'

export const useDestacamentoStore = defineStore('destacamento', {
  state: () => ({
    opciones: [] as DestacamentoOpcion[],
    actualId: null as number | null,
    cargado: false,
  }),
  actions: {
    /**
     * Con alcance global (SUPERVISOR_GENERAL) se listan todos los destacamentos
     * vía DESTACAMENTOS_VER (nombre real). Un LIDER_PRINCIPAL/LIDER_GRUPO no tiene
     * ese permiso — solo conoce sus propios `destacamentoIds` (sin nombre, ver
     * MeResponse), así que se muestran como "Destacamento #id" hasta que el
     * backend exponga una forma de resolver el nombre para un usuario con
     * alcance limitado.
     */
    async cargar() {
      if (this.cargado) return
      const permissions = usePermissionsStore()
      if (permissions.alcanceGlobal) {
        const destacamentos = await destacamentoService.listar()
        this.opciones = destacamentos.filter((d) => d.activo).map((d) => ({ id: d.id, nombre: d.nombre }))
      } else {
        this.opciones = [...permissions.destacamentoIds]
          .sort((a, b) => a - b)
          .map((id) => ({ id, nombre: `Destacamento #${id}` }))
      }
      this.cargado = true
      this.restaurarOEscogerPrimero()
    },
    restaurarOEscogerPrimero() {
      const guardado = Number(localStorage.getItem(ACTUAL_KEY))
      if (guardado && this.opciones.some((o) => o.id === guardado)) {
        this.actualId = guardado
        return
      }
      this.actualId = this.opciones[0]?.id ?? null
    },
    seleccionar(id: number) {
      this.actualId = id
      localStorage.setItem(ACTUAL_KEY, String(id))
    },
    clear() {
      this.opciones = []
      this.actualId = null
      this.cargado = false
    },
  },
})
