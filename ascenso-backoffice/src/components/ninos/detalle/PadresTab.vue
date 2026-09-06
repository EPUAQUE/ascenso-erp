<script setup lang="ts">
import { computed, ref } from 'vue'
import ActionIcon from '@/components/common/ActionIcon.vue'
import { usePermissionsStore } from '@/stores/permissions.store'
import { usuarioService } from '@/services/seguridad/UsuarioService'
import { ApiClientError } from '@/services/http/ApiClient'
import type { NinoPadre } from '@/types/actividades'

const props = defineProps<{
  listar: () => Promise<NinoPadre[]>
  vincular: (usuarioId: number) => Promise<NinoPadre>
  desvincular: (usuarioId: number) => Promise<void>
}>()

const permissions = usePermissionsStore()
const puedeEditar = computed(() => permissions.can('NINOS_EDITAR'))

const padres = ref<NinoPadre[]>([])
const cargando = ref(false)
const errorMessage = ref<string | null>(null)
const nombresPorId = ref<Map<number, string>>(new Map())

function nombreDe(usuarioId: number): string {
  return nombresPorId.value.get(usuarioId) ?? `Usuario #${usuarioId}`
}

async function cargar() {
  cargando.value = true
  errorMessage.value = null
  try {
    padres.value = await props.listar()
    const nombres = await usuarioService.resolverNombres(padres.value.map((p) => p.usuarioId))
    nombresPorId.value = new Map(nombres.map((n) => [n.id, n.nombre]))
  } catch (error) {
    errorMessage.value =
      error instanceof ApiClientError ? error.message : 'No se pudo cargar los padres vinculados.'
  } finally {
    cargando.value = false
  }
}

cargar()

const usuarioId = ref<number | null>(null)
const guardando = ref(false)
const formError = ref<string | null>(null)

async function onVincular() {
  if (guardando.value || !usuarioId.value) {
    formError.value = 'Ingresa el ID del usuario.'
    return
  }
  guardando.value = true
  formError.value = null
  try {
    const creado = await props.vincular(usuarioId.value)
    padres.value.push(creado)
    if (!nombresPorId.value.has(creado.usuarioId)) {
      const [nombre] = await usuarioService.resolverNombres([creado.usuarioId])
      if (nombre) nombresPorId.value.set(nombre.id, nombre.nombre)
    }
    usuarioId.value = null
  } catch (error) {
    formError.value = error instanceof ApiClientError ? error.message : 'No se pudo vincular al padre.'
  } finally {
    guardando.value = false
  }
}

async function onDesvincular(p: NinoPadre) {
  errorMessage.value = null
  try {
    await props.desvincular(p.usuarioId)
    padres.value = padres.value.filter((x) => x.usuarioId !== p.usuarioId)
  } catch (error) {
    errorMessage.value = error instanceof ApiClientError ? error.message : 'No se pudo desvincular al padre.'
  }
}
</script>

<template>
  <div class="max-w-lg space-y-4">
    <p class="text-sm text-mk-text-muted">Usuarios (rol PADRE) que pueden ver el progreso de este niño.</p>

    <p
      v-if="errorMessage"
      class="rounded-md bg-mk-danger/10 px-3 py-2 text-sm font-medium text-mk-danger"
      role="alert"
    >
      {{ errorMessage }}
    </p>

    <div class="mk-card overflow-hidden">
      <p v-if="cargando" class="px-4 py-6 text-center text-sm text-mk-text-muted">Cargando…</p>
      <p v-else-if="padres.length === 0" class="px-4 py-6 text-center text-sm text-mk-text-muted">
        Sin padres vinculados todavía.
      </p>
      <ul v-else class="divide-y divide-mk-border">
        <li
          v-for="p in padres"
          :key="p.usuarioId"
          class="flex items-center justify-between px-4 py-2.5 text-sm"
        >
          <span class="font-medium text-mk-text">{{ nombreDe(p.usuarioId) }}</span>
          <button
            v-if="puedeEditar"
            type="button"
            class="mk-row-btn mk-row-btn-danger"
            title="Desvincular"
            @click="onDesvincular(p)"
          >
            <ActionIcon name="x" />
          </button>
        </li>
      </ul>
    </div>

    <form v-if="puedeEditar" class="mk-card flex items-end gap-3 p-4" @submit.prevent="onVincular">
      <div class="flex-1">
        <label class="mb-1 block text-sm font-medium text-mk-text">ID del usuario</label>
        <input
          v-model.number="usuarioId"
          type="number"
          min="1"
          class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
        />
        <p v-if="formError" class="mt-1 text-sm font-medium text-mk-danger">{{ formError }}</p>
      </div>
      <button type="submit" :disabled="guardando" class="mk-btn mk-btn-primary">
        <ActionIcon name="plus" class="h-4 w-4" />
        {{ guardando ? 'Vinculando…' : 'Vincular' }}
      </button>
    </form>
  </div>
</template>
