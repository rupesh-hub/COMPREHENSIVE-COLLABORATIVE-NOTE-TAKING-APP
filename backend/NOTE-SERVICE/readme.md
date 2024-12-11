# Database Schema

## PROJECT
| Column       | Type    | Constraints              |
|--------------|---------|--------------------------|
| ID           | Long    | Primary Key              |
| PROJECT_ID   | String  | Unique, Not Null         |
| TITLE        | String  | Not Null                 |
| DESCRIPTION  | String  |                          |
| CREATED_AT   | Date    | Not Null                 |
| CREATED_BY   | String  | Not Null                 |
| UPDATED_AT   | Date    |                          |
| UPDATED_BY   | String  |                          |
| ENABLED      | Boolean | Not Null                 |

---

## NOTE
| Column     | Type    | Constraints                |
|------------|---------|----------------------------|
| ID         | Long    | Primary Key                |
| NOTE_ID    | String  | Unique, Not Null           |
| TITLE      | String  | Not Null                   |
| CONTENT    | Text    |                            |
| PROJECT_ID | Long    | Foreign Key (`PROJECT.ID`) |
| CREATED_AT | Date    | Not Null                   |
| CREATED_BY | String  | Not Null                   |
| UPDATED_AT | Date    |                            |
| UPDATED_BY | String  |                            |
| ENABLED    | Boolean | Not Null                   |

---

## DRAFT
| Column     | Type    | Constraints                |
|------------|---------|----------------------------|
| ID         | Long    | Primary Key                |
| DRAFT_ID   | String  | Unique, Not Null           |
| TITLE      | String  | Not Null                   |
| CONTENT    | Text    |                            |
| PROJECT_ID | Long    | Foreign Key (`PROJECT.ID`) |
| CREATED_AT | Date    | Not Null                   |
| CREATED_BY | String  | Not Null                   |
| UPDATED_AT | Date    |                            |
| UPDATED_BY | String  |                            |
| ENABLED    | Boolean | Not Null                   |

---

## IMAGE
| Column     | Type    | Constraints              |
|------------|---------|--------------------------|
| ID         | Long    | Primary Key              |
| NAME       | String  | Not Null                 |
| PATH       | String  | Not Null                 |
| SIZE       | Long    |                          |
| TYPE       | String  |                          |
| DRAFT_ID   | Long    | Foreign Key (`DRAFT.ID`) |
| NOTE_ID    | Long    | Foreign Key (`NOTE.ID`)  |
| CREATED_AT | Date    | Not Null                 |
| CREATED_BY | String  | Not Null                 |
| UPDATED_AT | Date    |                          |
| UPDATED_BY | String  |                          |
| ENABLED    | Boolean | Not Null                 |

---

## COLLABORATOR
| Column       | Type    | Constraints                  |
|--------------|---------|------------------------------|
| ID           | Long    | Primary Key                  |
| NAME         | String  | Not Null                     |
| EMAIL        | String  | Unique, Not Null             |
| USERNAME     | String  | Unique, Not Null             |
| PROFILE      | String  |                              |
| AUTHORITY_ID | Long    | Foreign Key (`AUTHORITY.ID`) |
| CREATED_AT   | Date    | Not Null                     |
| CREATED_BY   | String  | Not Null                     |
| UPDATED_AT   | Date    |                              |
| UPDATED_BY   | String  |                              |
| ENABLED      | Boolean | Not Null                     |

---

## AUTHORITY
| Column       | Type    | Constraints      |
|--------------|---------|------------------|
| ID           | Long    | Primary Key      |
| AUTHORITY_ID | String  | Unique, Not Null |
| NAME         | String  | Not Null         |
| CREATED_AT   | Date    | Not Null         |
| CREATED_BY   | String  | Not Null         |
| UPDATED_AT   | Date    |                  |
| UPDATED_BY   | String  |                  |
| ENABLED      | Boolean | Not Null         |

---

## PERMISSION
| Column        | Type    | Constraints                  |
|---------------|---------|------------------------------|
| ID            | Long    | Primary Key                  |
| PERMISSION_ID | String  | Unique, Not Null             |
| NAME          | String  | Not Null                     |
| AUTHORITY_ID  | Long    | Foreign Key (`AUTHORITY.ID`) |
| CREATED_AT    | Date    | Not Null                     |
| CREATED_BY    | String  | Not Null                     |
| UPDATED_AT    | Date    |                              |
| UPDATED_BY    | String  |                              |
| ENABLED       | Boolean | Not Null                     |

---

## PROJECTS_COLLABORATORS (Join Table)
| Column          | Type | Constraints                     |
|-----------------|------|---------------------------------|
| PROJECT_ID      | Long | Foreign Key (`PROJECT.ID`)      |
| COLLABORATOR_ID | Long | Foreign Key (`COLLABORATOR.ID`) |



