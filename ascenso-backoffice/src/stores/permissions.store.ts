import { defineStore } from 'pinia'
import type { PermissionCode } from '@/types/auth'

interface PermissionsState {
  permisos: Set<PermissionCode>
  destacamentoIds: Set<number>
  alcanceGlobal: boolean
}

export const usePermissionsStore = defineStore('permissions', {
  state: (): PermissionsState => ({
    permisos: new Set(),
    destacamentoIds: new Set(),
    alcanceGlobal: false,
  }),
  actions: {
    hydrate(permisos: PermissionCode[], destacamentoIds: number[], alcanceGlobal: boolean) {
      this.permisos = new Set(permisos)
      this.destacamentoIds = new Set(destacamentoIds)
      this.alcanceGlobal = alcanceGlobal
    },
    clear() {
      this.permisos = new Set()
      this.destacamentoIds = new Set()
      this.alcanceGlobal = false
    },
    can(codigo: PermissionCode): boolean {
      return this.permisos.has(codigo)
    },
    canAny(codigos: PermissionCode[]): boolean {
      return codigos.some((codigo) => this.permisos.has(codigo))
    },
    canAccessDestacamento(destacamentoId: number): boolean {
      return this.alcanceGlobal || this.destacamentoIds.has(destacamentoId)
    },
  },
})
