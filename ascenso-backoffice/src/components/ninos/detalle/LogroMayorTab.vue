<script setup lang="ts">
import { computed, ref } from 'vue'
import { usePermissionsStore } from '@/stores/permissions.store'
import { ApiClientError } from '@/services/http/ApiClient'
import type { LogroMayor } from '@/types/ninos'

const props = defineProps<{
  obtener: () => Promise<LogroMayor | null>
  otorgar: (fechaOtorgada: string) => Promise<LogroMayor>
}>()

const permissions = usePermissionsStore()
const puedeEditar = computed(() => permissions.can('NINOS_EDITAR'))

const logroMayor = ref<LogroMayor | null>(null)
const cargando = ref(false)
const errorMessage = ref<string | null>(null)
const fechaOtorgada = ref('')
const guardando = ref(false)

async function cargar() {
  cargando.value = true
  errorMessage.value = null
  try {
    logroMayor.value = await props.obtener()
  } catch (error) {
    errorMessage.value = error instanceof ApiClientError ? error.message : 'No se pudo cargar el logro mayor.'
  } finally {
    cargando.value = false
  }
}

cargar()

async function onOtorgar() {
  if (guardando.value || !fechaOtorgada.value) return
  guardando.value = true
  errorMessage.value = null
  try {
    logroMayor.value = await props.otorgar(fechaOtorgada.value)
  } catch (error) {
    errorMessage.value =
      error instanceof ApiClientError ? error.message : 'No se pudo otorgar el logro mayor.'
  } finally {
    guardando.value = false
  }
}
</script>

<template>
  <div class="mk-card max-w-md p-5">
    <p v-if="cargando" class="text-sm text-mk-text-muted">Cargando…</p>

    <template v-else-if="logroMayor">
      <p class="text-sm font-semibold text-mk-success">Logro mayor otorgado</p>
      <p class="mt-1 text-sm text-mk-text-muted">Fecha: {{ logroMayor.fechaOtorgada }}</p>
    </template>

    <template v-else>
      <p class="text-sm text-mk-text-muted">Este niño todavía no ha recibido el logro mayor.</p>

      <form v-if="puedeEditar" class="mt-4 space-y-3" @submit.prevent="onOtorgar">
        <div>
          <label class="mb-1 block text-sm font-medium text-mk-text">Fecha de otorgamiento</label>
          <input
            v-model="fechaOtorgada"
            type="date"
            class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
          />
        </div>
        <button type="submit" :disabled="guardando" class="mk-btn mk-btn-primary">
          {{ guardando ? 'Guardando…' : 'Otorgar logro mayor' }}
        </button>
      </form>
    </template>

    <p
      v-if="errorMessage"
      class="mt-3 rounded-md bg-mk-danger/10 px-3 py-2 text-sm font-medium text-mk-danger"
      role="alert"
    >
      {{ errorMessage }}
    </p>
  </div>
</template>
