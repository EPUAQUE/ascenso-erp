<script setup lang="ts">
import { computed, ref } from 'vue'
import ModalDialog from '@/components/common/ModalDialog.vue'
import ActionIcon from '@/components/common/ActionIcon.vue'
import { usePermissionsStore } from '@/stores/permissions.store'
import { grupoService } from '@/services/catalogo/GrupoService'
import { ApiClientError } from '@/services/http/ApiClient'
import type { Grupo } from '@/types/catalogo'

const props = defineProps<{ grupos: Grupo[] }>()
const emit = defineEmits<{ (e: 'actualizado', grupo: Grupo): void }>()

const permissions = usePermissionsStore()
const puedeEditar = computed(() => permissions.can('CATALOGO_EDITAR'))

const gruposOrdenados = computed(() => [...props.grupos].sort((a, b) => a.orden - b.orden))

const formOpen = ref(false)
const grupoEnEdicion = ref<Grupo | null>(null)
const nombre = ref('')
const edadMin = ref(0)
const edadMax = ref(0)
const orden = ref(0)
const descripcion = ref('')
const guardando = ref(false)
const formError = ref<string | null>(null)

function abrirEditar(g: Grupo) {
  grupoEnEdicion.value = g
  nombre.value = g.nombre
  edadMin.value = g.edadMin
  edadMax.value = g.edadMax
  orden.value = g.orden
  descripcion.value = g.descripcion ?? ''
  formError.value = null
  formOpen.value = true
}

async function onSubmit() {
  if (guardando.value || !grupoEnEdicion.value || !nombre.value.trim()) {
    formError.value = 'Completa los campos requeridos.'
    return
  }
  guardando.value = true
  formError.value = null
  try {
    const actualizado = await grupoService.actualizar(grupoEnEdicion.value.id, {
      nombre: nombre.value.trim(),
      edadMin: edadMin.value,
      edadMax: edadMax.value,
      orden: orden.value,
      descripcion: descripcion.value.trim() || null,
    })
    emit('actualizado', actualizado)
    formOpen.value = false
  } catch (error) {
    formError.value = error instanceof ApiClientError ? error.message : 'No se pudo guardar el grupo.'
  } finally {
    guardando.value = false
  }
}
</script>

<template>
  <div class="space-y-4">
    <p class="text-sm text-mk-text-muted">
      Catálogo global fijo — mismos grupos para todos los destacamentos. No se pueden crear ni eliminar, solo
      editar.
    </p>

    <div class="mk-card mk-scroll-x overflow-x-auto">
      <table class="w-full text-left text-sm">
        <thead>
          <tr
            class="border-b border-mk-border text-xs font-semibold uppercase tracking-wider text-mk-text-muted"
          >
            <th class="px-4 py-3">Orden</th>
            <th class="px-4 py-3">Nombre</th>
            <th class="px-4 py-3">Rango de edad</th>
            <th class="px-4 py-3">Descripción</th>
            <th class="px-4 py-3 text-right">Acciones</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-mk-border">
          <tr v-for="g in gruposOrdenados" :key="g.id">
            <td class="mk-num px-4 py-2.5 text-mk-text-muted">{{ g.orden }}</td>
            <td class="px-4 py-2.5 font-medium text-mk-text">{{ g.nombre }}</td>
            <td class="mk-num px-4 py-2.5 text-mk-text-muted">{{ g.edadMin }}–{{ g.edadMax }} años</td>
            <td class="px-4 py-2.5 text-mk-text-muted">{{ g.descripcion ?? '—' }}</td>
            <td class="px-4 py-2.5">
              <div v-if="puedeEditar" class="mk-row-actions justify-end">
                <button type="button" class="mk-row-btn" title="Editar" @click="abrirEditar(g)">
                  <ActionIcon name="edit" />
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <ModalDialog v-model="formOpen" title="Editar grupo">
      <form class="space-y-4" @submit.prevent="onSubmit">
        <p
          v-if="formError"
          class="rounded-md bg-mk-danger/10 px-3 py-2 text-sm font-medium text-mk-danger"
          role="alert"
        >
          {{ formError }}
        </p>

        <div>
          <label class="mb-1 block text-sm font-medium text-mk-text">Nombre</label>
          <input
            v-model="nombre"
            type="text"
            maxlength="50"
            class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
          />
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="mb-1 block text-sm font-medium text-mk-text">Edad mínima</label>
            <input
              v-model.number="edadMin"
              type="number"
              min="0"
              class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
            />
          </div>
          <div>
            <label class="mb-1 block text-sm font-medium text-mk-text">Edad máxima</label>
            <input
              v-model.number="edadMax"
              type="number"
              min="0"
              class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
            />
          </div>
        </div>

        <div>
          <label class="mb-1 block text-sm font-medium text-mk-text">Orden</label>
          <input
            v-model.number="orden"
            type="number"
            min="1"
            class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
          />
        </div>

        <div>
          <label class="mb-1 block text-sm font-medium text-mk-text">Descripción (opcional)</label>
          <input
            v-model="descripcion"
            type="text"
            maxlength="255"
            class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
          />
        </div>

        <div class="flex justify-end gap-2 pt-2">
          <button type="button" class="mk-btn mk-btn-ghost" @click="formOpen = false">Cancelar</button>
          <button type="submit" :disabled="guardando" class="mk-btn mk-btn-primary">
            {{ guardando ? 'Guardando…' : 'Guardar' }}
          </button>
        </div>
      </form>
    </ModalDialog>
  </div>
</template>
