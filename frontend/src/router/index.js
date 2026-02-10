import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '../store/auth';

// Login route
const Login = () => import('../views/Login.vue');

// Main layout with sidebar
const Index = () => import('../views/Index.vue');

// My Forms routes
const MyForms = () => import('../views/MyForms.vue');

// For My Action routes
const ForMyAction = () => import('../views/ForMyAction.vue');

// Form management routes
const CreateForm = () => import('../views/CreateForm.vue');
const EditForm = () => import('../views/EditForm.vue');
const ResubmitForm = () => import('../views/ResubmitForm.vue');
const CompletedForm = () => import('../views/CompletedForm.vue');
const ProcessingForm = () => import('../views/ProcessingForm.vue');
const ViewForm = () => import('../views/ViewForm.vue');
const SubmitForm = () => import('../views/SubmitForm.vue');

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: Index,
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue')
      },
      {
        path: 'my-forms',
        name: 'MyForms',
        component: MyForms
      },
      {
        path: 'for-my-action',
        name: 'ForMyAction',
        component: ForMyAction
      },
      {
        path: 'create-form',
        name: 'CreateForm',
        component: CreateForm
      },
      {
        path: 'edit-form/:id',
        name: 'EditForm',
        component: EditForm,
        props: true
      },
      {
        path: 'view-form/:id',
        name: 'ViewForm',
        component: ViewForm,
        props: true
      },
      {
        path: 'submit-form/:id',
        name: 'SubmitForm',
        component: SubmitForm,
        props: true
      },
      {
        path: 'resubmit-form/:id',
        name: 'ResubmitForm',
        component: ResubmitForm,
        props: true
      },
      {
        path: 'completed-form/:id',
        name: 'CompletedForm',
        component: CompletedForm,
        props: true
      },
      {
        path: 'processing-form/:id',
        name: 'ProcessingForm',
        component: ProcessingForm,
        props: true
      }
    ]
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();
  
  // Load user from localStorage on navigation
  authStore.loadUser();
  
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login');
  } else if (to.path === '/login' && authStore.isAuthenticated) {
    next('/');
  } else {
    next();
  }
});

export default router;