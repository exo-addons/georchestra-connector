<template>
  <div>
    <exo-confirm-dialog
      ref="deleteConfirmDialog"
      :title="$t('georchestra.drawer.delete.confirm.title')"
      :message="$t('georchestra.drawer.delete.confirm.message')"
      :ok-label="$t('georchestra.drawer.delete.confirm.ok')"
      :cancel-label="$t('georchestra.drawer.delete.confirm.cancel')"
      @ok="confirmDelete" />
    <exo-drawer
      id="georchestraDrawer"
      ref="georchestraSpaceSettingsDrawer"
      right
      allow-expand
      @closed="close">
      <template slot="title">
        <div class="d-flex">
          <v-icon
            size="16"
            class="clickable"
            :aria-label="$t('geochestra.close.label')"
            @click="close()">
            fas fa-arrow-left
          </v-icon>
          <span>{{ $t('georchestra.drawer.title') }}</span>
        </div>
      </template>
      <template slot="content">
        <v-form>
          <v-card-text class="d-flex">
            <v-label>
              <span class="text-color font-weight-bold text-start text-truncate-2">
                {{ $t('georchestra.drawer.content.title') }}
              </span>
              <span class="caption">{{ $t('georchestra.drawer.content.actualRole') }} : {{ this.initialRole && this.initialRole.length !==0 ? this.initialRole : $t('georchestra.role.notconfigured') }}</span>
            </v-label>
          </v-card-text>
          <v-card-text class="d-flex py-0">
            <span class="caption">{{ $t('georchestra.drawer.content.description') }}</span>
          </v-card-text>
          <v-card-text class="d-flex py-0">
            <v-text-field
              v-model="role"
              :placeholder="$t('georchestra.drawer.content.placeholder')"
              class="pt-0"
              type="text"
              outlined
              dense />
          </v-card-text>
          <v-card-text class="d-flex py-0">
            <v-label>
              <span class="caption">{{ $t('georchestra.drawer.content.hint1') }}</span>
            </v-label>
          </v-card-text>
          <v-card-text class="d-flex py-0">
            <v-label>
              <span class="caption">{{ $t('georchestra.drawer.content.hint2') }}</span>
            </v-label>
          </v-card-text>
        </v-form>
      </template>
      <template slot="footer">
        <div class="d-flex justify-end">
          <v-btn
            :disabled="!canDelete"
            class="ms-2 me-auto error-color"
            @click="deleteRole"
            outlined>
            {{ $t('georchestra.label.btn.delete') }}
          </v-btn>
          <v-btn
            class="btn ms-2"
            @click="close">
            {{ $t('georchestra.label.btn.cancel') }}
          </v-btn>
          <v-btn
            :disabled="disabled"
            :loading="loading"
            v-if="!roleChecked"
            @click="testRole"
            class="btn btn-primary ms-2">
            {{ $t('georchestra.label.btn.test') }}
          </v-btn>
          <v-btn
            v-else
            @click="save"
            class="btn btn-primary ms-2">
            {{ $t('georchestra.label.btn.save') }}
          </v-btn>
        </div>
      </template>
    </exo-drawer>
  </div>
</template>
<script>
export default {
  data () {
    return {
      role: '',
      initialRole: '',
      roleChecked: false,
      loading: false
    };
  },
  created() {
    this.$root.$on('open-georchestra-space-settings-drawer', role => {
      this.open(role);
    });
  },
  computed: {
    disabled() {
      return !this.role || this.role.length === 0 || this.initialRole === this.role;
    },
    canDelete() {
      return this.initialRole && this.initialRole.length !== 0;
    },
  },
  watch: {
    role: function() {
      this.roleChecked=false;
    }
  },
  methods: {
    open(role) {
      this.role=role;
      this.initialRole=role;
      this.$nextTick().then(() => {
        if (this.$refs.georchestraSpaceSettingsDrawer) {
          this.$refs.georchestraSpaceSettingsDrawer.open();
        }
      });
    },
    close() {
      this.$nextTick().then(() => {
        this.$root.$emit('close-georchestra-space-settings-drawer');
        this.$refs.georchestraSpaceSettingsDrawer.close();
      });
    },
    testRole() {
      this.loading=true;
      this.$root.$georchestraService.validateRole(this.role,eXo.env.portal.spaceId).then((data) => {
        if (data) {
          this.roleChecked=true;
        } else {
          this.$root.$emit('alert-message', this.$t('georchestra.drawer.error.test.role'), 'error');
        }
      }).catch(() => {
        this.$root.$emit('alert-message', this.$t('georchestra.drawer.error.test.role'), 'error');
      }).finally(() => {
        this.loading=false;
      });
    },
    save() {
      this.$root.$georchestraService.saveRole(this.role,eXo.env.portal.spaceId).then(() => {
        this.$root.$emit('alert-message', this.$t('georchestra.drawer.success.save.role'), 'success');
        this.close();
      }).catch(() => {
        this.$root.$emit('alert-message', this.$t('georchestra.drawer.error.save.role'), 'error');
      });
    },
    deleteRole() {
      this.$refs.deleteConfirmDialog.open();
    },
    confirmDelete() {
      this.$root.$georchestraService.deleteRole(this.initialRole,eXo.env.portal.spaceId).then(() => {
        this.$root.$emit('alert-message', this.$t('georchestra.drawer.success.delete.role'), 'success');
        this.close();
      }).catch(() => {
        this.$root.$emit('alert-message', this.$t('georchestra.drawer.error.delete.role'), 'error');
      });
    }
  }
};
</script>
