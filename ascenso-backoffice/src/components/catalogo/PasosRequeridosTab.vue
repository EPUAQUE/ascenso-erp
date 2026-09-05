<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import ModalDialog from '@/components/common/ModalDialog.vue'
import ActionIcon from '@/components/common/ActionIcon.vue'
import { usePermissionsStore } from '@/stores/permissions.store'
import { pasoRequeridoService } from '@/services/catalogo/PasoRequeridoService'
import { ApiClientError } from '@/services/http/ApiClient'
import type { AnioPrograma, Grupo, PasoRequerido } from '@/types/catalogo'

const props = defineProps<{ grupos: Grupo[]; aniosPrograma: AnioPrograma[] }>()

const permissions = usePermissionsStore()
const puedeEditar = computed(() => permissions.can('CATALOGO_EDITAR'))

const pasos = ref<PasoRequerido[]>([])
const cargando = ref(false)
const errorMessage = ref<string | null>(null)
const filtroGrupoId = ref<number | null>(null)
const filtroAnioProgramaId = ref<number | null>(null)

const aniosDelFiltro = computed(() =>
  props.aniosPrograma.filter((a) => a.grupoId === filtroGrupoId.value).sort((a, b) => a.numero - b.numero),
)

watch(filtroGrupoId, () => {
  filtroAnioProgramaId.value = null
})

function nombreGrupo(id: number): string {
  return props.grupos.find((g) => g.id === id)?.nombre ?? `Grupo #${id}`
}

function etiquetaAnio(id: number): string {
  const a = props.aniosPrograma.find((x) => x.id === id)
  return a ? `${nombreGrupo(a.grupoId)} · Año ${a.numero}` : `Año #${id}`
}

async function cargar() {
  cargando.value = true
  errorMessage.value = null
  try {
    pasos.value = await pasoRequeridoService.listar(filtroAnioProgramaId.value ?? undefined)
  } catch (error) {
    errorMessage.value =
      error instanceof ApiClientError ? error.message : 'No se pudo cargar los pasos requeridos.'
  } finally {
    cargando.value = false
  }
}

watch(filtroAnioProgramaId, cargar)
cargar()

const formOpen = ref(false)
const pasoEnEdicion = ref<PasoRequerido | null>(null)
const formGrupoId = ref<number | null>(null)
const anioProgramaId = ref<number | null>(null)
const descripcion = ref('')
const orden = ref(1)
const guardando = ref(false)
const formError = ref<string | null>(null)

const aniosDelForm = computed(() =>
  props.aniosPrograma.filter((a) => a.grupoId === formGrupoId.value).sort((a, b) => a.numero - b.numero),
)

watch(formGrupoId, () => {
  if (!aniosDelForm.value.some((a) => a.id === anioProgramaId.value)) {
    anioProgramaId.value = aniosDelForm.value[0]?.id ?? null
  }
})

function abrirCrear() {
  pasoEnEdicion.value = null
  formGrupoId.value = filtroGrupoId.value ?? props.grupos[0]?.id ?? null
  anioProgramaId.value =
    filtroAnioProgramaId.value ?? props.aniosPrograma.find((a) => a.grupoId === formGrupoId.value)?.id ?? null
  descripcion.value = ''
  orden.value = 1
  formError.value = null
  formOpen.value = true
}

function abrirEditar(p: PasoRequerido) {
  pasoEnEdicion.value = p
  const anio = props.aniosPrograma.find((a) => a.id === p.anioProgramaId)
  formGrupoId.value = anio?.grupoId ?? null
  anioProgramaId.value = p.anioProgramaId
  descripcion.value = p.descripcion
  orden.value = p.orden
  formError.value = null
  formOpen.value = true
}

async function onSubmit() {
  if (guardando.value || !anioProgramaId.value || !descripcion.value.trim()) {
    formError.value = 'Completa los campos requeridos.'
    return
  }
  guardando.value = true
  formError.value = null
  const request = {
    anioProgramaId: anioProgramaId.value,
    descripcion: descripcion.value.trim(),
    orden: orden.value,
  }
  try {
    if (pasoEnEdicion.value) {
      const actualizado = await pasoRequeridoService.actualizar(pasoEnEdicion.value.id, request)
      const idx = pasos.value.findIndex((p) => p.id === actualizado.id)
      if (idx >= 0) pasos.value[idx] = actualizado
    } else {
      const creado = await pasoRequeridoService.crear(request)
      pasos.value.push(creado)
    }
    formOpen.value = false
  } catch (error) {
    formError.value =
      error instanceof ApiClientError ? error.message : 'No se pudo guardar el paso requerido.'
  } finally {
    guardando.value = false
  }
}
</script>

<template>
  <div class="space-y-4">
    <div class="flex flex-wrap items-center justify-between gap-3">
      <div class="flex flex-wrap items-center gap-3">
        <label class="flex items-center gap-2 text-sm">
          <span class="text-mk-text-muted">Grupo</span>
          <select
            v-model.number="filtroGrupoId"
            class="mk-input rounded-md border border-mk-border px-2 py-1.5 text-sm"
          >
            <option :value="null">Todos</option>
            <option v-for="g in grupos" :key="g.id" :value="g.id">{{ g.nombre }}</option>
          </select>
        </label>
        <label v-if="filtroGrupoId" class="flex items-center gap-2 text-sm">
          <span class="text-mk-text-muted">Año</span>
          <select
            v-model.number="filtroAnioProgramaId"
            class="mk-input rounded-md border border-mk-border px-2 py-1.5 text-sm"
          >
            <option :value="null">Todos</option>
            <option v-for="a in aniosDelFiltro" :key="a.id" :value="a.id">Año {{ a.numero }}</option>
          </select>
        </label>
      </div>

      <button v-if="puedeEditar" type="button" class="mk-btn mk-btn-primary" @click="abrirCrear">
        <ActionIcon name="plus" class="h-4 w-4" />
        Nuevo paso requerido
      </button>
    </div>

    <p
      v-if="errorMessage"
      class="rounded-md bg-mk-danger/10 px-3 py-2 text-sm font-medium text-mk-danger"
      role="alert"
    >
      {{ errorMessage }}
    </p>

    <div class="mk-card mk-scroll-x overflow-x-auto">
      <table class="w-full text-left text-sm">
        <thead>
          <tr
            class="border-b border-mk-border text-xs font-semibold uppercase tracking-wider text-mk-text-muted"
          >
            <th class="px-4 py-3">Descripción</th>
            <th class="px-4 py-3">Orden</th>
            <th class="px-4 py-3">Año de programa</th>
            <th class="px-4 py-3 text-right">Acciones</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-mk-border">
          <tr v-if="cargando">
            <td colspan="4" class="px-4 py-6 text-center text-mk-text-muted">Cargando…</td>
          </tr>
          <tr v-else-if="pasos.length === 0">
            <td colspan="4" class="px-4 py-6 text-center text-mk-text-muted">
              No hay pasos requeridos para mostrar.
            </td>
          </tr>
          <tr v-for="p in pasos" v-else :key="p.id">
            <td class="px-4 py-2.5 font-medium text-mk-text">{{ p.descripcion }}</td>
            <td class="mk-num px-4 py-2.5 text-mk-text-muted">{{ p.orden }}</td>
            <td class="px-4 py-2.5 text-mk-text-muted">{{ etiquetaAnio(p.anioProgramaId) }}</td>
            <td class="px-4 py-2.5">
              <div v-if="puedeEditar" class="mk-row-actions justify-end">
                <button type="button" class="mk-row-btn" title="Editar" @click="abrirEditar(p)">
                  <ActionIcon name="edit" />
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <ModalDialog v-model="formOpen" :title="pasoEnEdicion ? 'Editar paso requerido' : 'Nuevo paso requerido'">
      <form class="space-y-4" @submit.prevent="onSubmit">
        <p
          v-if="formError"
          class="rounded-md bg-mk-danger/10 px-3 py-2 text-sm font-medium text-mk-danger"
          role="alert"
        >
          {{ formError }}
        </p>

        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="mb-1 block text-sm font-medium text-mk-text">Grupo</label>
            <select
              v-model.number="formGrupoId"
              :disabled="pasoEnEdicion !== null"
              class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm disabled:opacity-60"
            >
              <option v-for="g in grupos" :key="g.id" :value="g.id">{{ g.nombre }}</option>
            </select>
          </div>
          <div>
            <label class="mb-1 block text-sm font-medium text-mk-text">Año de programa</label>
            <select
              v-model.number="anioProgramaId"
              :disabled="pasoEnEdicion !== null"
              class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm disabled:opacity-60"
            >
              <option v-for="a in aniosDelForm" :key="a.id" :value="a.id">Año {{ a.numero }}</option>
            </select>
          </div>
        </div>

        <div>
          <label class="mb-1 block text-sm font-medium text-mk-text">Descripción</label>
          <input
            v-model="descripcion"
            type="text"
            maxlength="255"
            class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
          />
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
