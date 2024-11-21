import { Routes } from '@angular/router';

export const NOTES_ROUTES: Routes = [
    {
      path: 'notes',
      loadComponent: () => import('./components/note/note.component')
        .then(m => m.NoteComponent),
      children: [
        {
          path: '',
          pathMatch: 'full',
          redirectTo: 'all'
        },
        {
          path: 'all',
          loadComponent: () => import('./components/note-list/note-list.component')
            .then(m => m.NoteListComponent)
        },
        {
          path: 'new/:projectId',
          loadComponent: () => import('./components/note-editor/note-editor.component')
            .then(m => m.NoteEditorComponent)
        },
        {
          path: 'edit/:id',
          loadComponent: () => import('./components/note-editor/note-editor.component')
            .then(m => m.NoteEditorComponent)
        },
        {
          path: 'draft/:projectId',
          loadComponent: () => import('./components/draft/draft.component')
            .then(m => m.DraftComponent)
        }
      ]
    }
  ];