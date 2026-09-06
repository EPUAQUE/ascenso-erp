import { defineStore } from 'pinia'
import { authService } from '@/services/auth/AuthService'

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
     * `/auth/mis-destacamentos` resuelve el nombre real de los destacamentos del
     * usuario autenticado (propios si es LIDER_PRINCIPAL/LIDER_GRUPO, el catálogo
     * completo si tiene alcance global) sin requerir DESTACAMENTOS_VER — autoservicio,
     * mismo mecanismo que `/auth/me`.
     */
    async cargar() {
      if (this.cargado) return
      const destacamentos = await authService.misDestacamentos()
      this.opciones = destacamentos.filter((d) => d.activo).map((d) => ({ id: d.id, nombre: d.nombre }))
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
