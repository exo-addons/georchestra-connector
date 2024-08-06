<template>
  <v-app>
    <v-card
      class="card-border-radius"
      flat>
      <v-list>
        <v-list-item>
          <v-list-item-content>
            <v-list-item-title class="title text-color">
              {{ $t('georchestra.title') }}
            </v-list-item-title>
            <v-list-item-title>
              {{ $t('georchestra.text') }}
            </v-list-item-title>
            <v-list-item-subtitle v-if="roleConfigured">
              {{ $t('georchestra.role') }} : {{ this.role }}
            </v-list-item-subtitle>
            <v-list-item-subtitle v-else>
              {{ $t('georchestra.role') }} : {{ $t('georchestra.role.notconfigured') }}
            </v-list-item-subtitle>
            <v-list-item-subtitle v-if="this.nbItemInQueueForSpace != 0">
              {{ this.nbItemInQueueForSpace }} {{ this.nbItemInQueueForSpace == 1 ? $t('georchestra.userToSync') : $t('georchestra.usersToSync') }}
            </v-list-item-subtitle>
          </v-list-item-content>
          <v-list-item-action>
            <v-btn
              small
              icon
              @click="openDrawer">
              <v-icon size="18" class="icon-default-color">fa-edit</v-icon>
            </v-btn>
          </v-list-item-action>
        </v-list-item>
      </v-list>
      <georchestra-space-settings-drawer />
    </v-card>
  </v-app>
</template>
<script>
export default {
  data () {
    return {
      role: '',
      nbItemInQueueForSpace: 0
    };
  },
  created() {
    this.getRole();
    this.$root.$on('close-georchestra-space-settings-drawer', this.getRole);
  },
  computed: {
    roleConfigured() {
      return this.role && this.role.length !==0;
    }
  },
  methods: {
    openDrawer() {
      this.$root.$emit('open-georchestra-space-settings-drawer',this.role);
    },
    getRole() {
      this.$root.$georchestraService.getRole(eXo.env.portal.spaceId).then((data) => {
        if (data.role) {
          this.role=data.role;
        } else {
          this.role='';
        }
      });
      this.$root.$georchestraService.getNbItemInQueueForSpace(eXo.env.portal.spaceId).then((data) => {
        if (data) {
          this.nbItemInQueueForSpace=data;
        } else {
          this.nbItemInQueueForSpace=0;
        }
      });
    }
  },

};
</script>

