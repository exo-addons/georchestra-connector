/*
 * Copyright (C) 2024 eXo Platform SAS.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
import GeorchestraSpaceSettings from './components/GeorchestraSpaceSettings.vue';
import GeorchestraSpaceSettingsDrawer from './components/GeorchestraSpaceSettingsDrawer.vue';
const components = {
  'georchestra-space-settings': GeorchestraSpaceSettings,
  'georchestra-space-settings-drawer': GeorchestraSpaceSettingsDrawer
};

for (const key in components) {
  Vue.component(key, components[key]);
}

import * as georchestraService from './GeorchestraService.js';

if (!Vue.prototype.$georchestraService) {
  window.Object.defineProperty(Vue.prototype, '$georchestraService', {
    value: georchestraService,
  });
}
