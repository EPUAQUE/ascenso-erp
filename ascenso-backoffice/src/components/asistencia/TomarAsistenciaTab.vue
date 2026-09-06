<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useDestacamentoStore } from '@/stores/destacamento.store'
import { usePermissionsStore } from '@/stores/permissions.store'
import { ninoService } from '@/services/ninos/NinoService'
import { asistenciaService } from '@/services/ninos/AsistenciaService'
import { trimestreService } from '@/services/ninos/TrimestreService'
import { ApiClientError } from '@/services/http/ApiClient'
import ActionIcon from '@/components/common/ActionIcon.vue'
import type { Trimestre } from '@/types/ninos'

const destacamentoStore = useDestacamentoStore()
const permissions = usePermissionsStore()
const puedeEditar = computed(() => permissions.can('NINOS_EDITAR'))

const trimestres = ref<Trimestre[]>([])
const trimestreId = ref<number | null>(null)
const fecha = ref(new Date().toISOString().slice(0, 10))

interface FilaAsistencia {
  ninoId: number
  nombreCompleto: string
  asistenciaId: number | null
  presente: boolean
  persistedPresente: boolean | null
  guardando: boolean
  error: string | null
}

const filas = ref<FilaAsistencia[]>([])
const cargando = ref(false)
const errorMessage = ref<string | null>(null)
const guardandoTodo = ref(false)

function trimestreAutomatico(): number | null {
  const hoy = new Date().toISOString().slice(0, 10)
  return (
    trimestres.value.find((t) => t.fechaInicio <= hoy && hoy <= t.fechaFin)?.id ??
    trimestres.value[0]?.id ??
    null
  )
}

async function cargarTrimestres() {
  try {
    trimestres.value = await trimestreService.listar()
    if (trimestreId.value === null) trimestreId.value = trimestreAutomatico()
  } catch (error) {
    errorMessage.value = error instanceof ApiClientError ? error.message : 'No se pudo cargar los trimestres.'
  }
}

onMounted(cargarTrimestres)

async function cargarRoster() {
  const destacamentoId = destacamentoStore.actualId
  if (!destacamentoId) {
    filas.value = []
    return
  }
  cargando.value = true
  errorMessage.value = null
  try {
    const ninos = (await ninoService.listar(destacamentoId)).filter((n) => n.activo)
    const listas = await Promise.all(ninos.map((n) => asistenciaService.listar(destacamentoId, n.id)))
    filas.value = ninos.map((n, i) => {
      const registro = listas[i].find((a) => a.fecha === fecha.value) ?? null
      return {
        ninoId: n.id,
        nombreCompleto: n.nombreCompleto,
        asistenciaId: registro?.id ?? null,
        presente: registro?.presente ?? true,
        persistedPresente: registro?.presente ?? null,
        guardando: false,
        error: null,
      }
    })
  } catch (error) {
    errorMessage.value =
      error instanceof ApiClientError ? error.message : 'No se pudo cargar la lista de niños.'
  } finally {
    cargando.value = false
  }
}

watch([() => destacamentoStore.actualId, fecha], cargarRoster, { immediate: true })

async function guardarFila(fila: FilaAsistencia) {
  const destacamentoId = destacamentoStore.actualId
  if (!destacamentoId || !trimestreId.value) return
  fila.guardando = true
  fila.error = null
  try {
    if (fila.asistenciaId === null) {
      const creado = await asistenciaService.registrar(destacamentoId, fila.ninoId, {
        trimestreId: trimestreId.value,
        fecha: fecha.value,
        presente: fila.presente,
      })
      fila.asistenciaId = creado.id
    } else {
      await asistenciaService.corregir(destacamentoId, fila.ninoId, fila.asistenciaId, {
        presente: fila.presente,
      })
    }
    fila.persistedPresente = fila.presente
  } catch (error) {
    fila.error = error instanceof ApiClientError ? error.message : 'No se pudo guardar.'
  } finally {
    fila.guardando = false
  }
}

const filasPendientes = computed(() =>
  filas.value.filter((f) => f.asistenciaId === null || f.presente !== f.persistedPresente),
)

async function guardarTodo() {
  guardandoTodo.value = true
  try {
    await Promise.all(filasPendientes.value.map((f) => guardarFila(f)))
  } finally {
    guardandoTodo.value = false
  }
}
</script>

<template>
  <div class="space-y-4">
    <div class="flex flex-wrap items-center gap-3">
      <label v-if="destacamentoStore.opciones.length > 1" class="flex items-center gap-2 text-sm">
        <span class="text-mk-text-muted">Destacamento</span>
        <select
          :value="destacamentoStore.actualId ?? undefined"
          class="mk-input rounded-md border border-mk-border px-2 py-1.5 text-sm"
          @change="destacamentoStore.seleccionar(Number(($event.target as HTMLSelectElement).value))"
        >
          <option v-for="o in destacamentoStore.opciones" :key="o.id" :value="o.id">{{ o.nombre }}</option>
        </select>
      </label>
      <span v-else-if="destacamentoStore.opciones.length === 1" class="text-sm text-mk-text-muted">
        {{ destacamentoStore.opciones[0].nombre }}
      </span>

      <label class="flex items-center gap-2 text-sm">
        <span class="text-mk-text-muted">Trimestre</span>
        <select
          v-model.number="trimestreId"
          class="mk-input rounded-md border border-mk-border px-2 py-1.5 text-sm"
        >
          <option v-for="t in trimestres" :key="t.id" :value="t.id">
            {{ t.anioCalendario }} · Trimestre {{ t.numero }}
          </option>
        </select>
      </label>

      <label class="flex items-center gap-2 text-sm">
        <span class="text-mk-text-muted">Fecha</span>
        <input
          v-model="fecha"
          type="date"
          class="mk-input rounded-md border border-mk-border px-2 py-1.5 text-sm"
        />
      </label>

      <button
        v-if="puedeEditar"
        type="button"
        class="mk-btn mk-btn-primary ml-auto"
        :disabled="guardandoTodo || filasPendientes.length === 0"
        @click="guardarTodo"
      >
        {{ guardandoTodo ? 'Guardando…' : `Guardar todo (${filasPendientes.length})` }}
      </button>
    </div>

    <p v-if="!trimestreId" class="rounded-md bg-mk-pending/10 px-3 py-2 text-sm font-medium" role="alert">
      No hay trimestres creados — crea uno en Trimestres para poder registrar asistencia.
    </p>

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
            <th class="px-4 py-3">Nombre</th>
            <th class="px-4 py-3">Presente</th>
            <th class="px-4 py-3">Estado</th>
            <th class="px-4 py-3 text-right">Acciones</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-mk-border">
          <tr v-if="cargando">
            <td colspan="4" class="px-4 py-6 text-center text-mk-text-muted">Cargando…</td>
          </tr>
          <tr v-else-if="filas.length === 0">
            <td colspan="4" class="px-4 py-6 text-center text-mk-text-muted">
              No hay niños activos en este destacamento.
            </td>
          </tr>
          <tr v-for="f in filas" v-else :key="f.ninoId">
            <td class="px-4 py-2.5 font-medium text-mk-text">{{ f.nombreCompleto }}</td>
            <td class="px-4 py-2.5">
              <label class="flex items-center gap-2">
                <input
                  v-model="f.presente"
                  type="checkbox"
                  class="rounded border-mk-border"
                  :disabled="!puedeEditar"
                />
                {{ f.presente ? 'Presente' : 'Ausente' }}
              </label>
            </td>
            <td class="px-4 py-2.5 text-mk-text-muted">
              <span v-if="f.error" class="text-mk-danger">{{ f.error }}</span>
              <span v-else-if="f.asistenciaId === null">Sin registrar</span>
              <span v-else-if="f.presente === f.persistedPresente">Guardado</span>
              <span v-else>Cambio sin guardar</span>
            </td>
            <td class="px-4 py-2.5">
              <div v-if="puedeEditar" class="mk-row-actions justify-end">
                <button
                  type="button"
                  class="mk-row-btn"
                  title="Guardar"
                  :disabled="f.guardando || (f.asistenciaId !== null && f.presente === f.persistedPresente)"
                  @click="guardarFila(f)"
                >
                  <ActionIcon name="check" />
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
