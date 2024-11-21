//Collaborator
export interface Collaborator {
  id: number;
  collaboratorId: string;
  userId: string;
  createAt: string;
  modifiedAt: string;
  createdBy: User;
  modifiedBy: User;
  authority: Authority;
}

//Authority
export interface Authority{
    id: number;
    name: string;
}

//Permission
export interface Permission {
    id: number;
    authority: Authority;
    name: string;
}

//User
export interface User {
  name: string;
  username: string;
  email: string;
  profile: string;
}
