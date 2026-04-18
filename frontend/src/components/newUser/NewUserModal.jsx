import { useState } from "react";
import "../../../public/newUserModal.css";
import { useAuthContext } from "../../context/AuthContext";
import { createUser } from "../../api/auth-api";
import { useForm } from "../../hooks/useForm";

export default function NewUserModal({onCreate,onClose }) {
  const { accessToken } = useAuthContext();

  const roleMapping = {
    Администратор: "ADMIN",
    Автор: "AUTHOR",
    Рецензент: "REVIEWER",
    Читател: "READER",
  };

  const initialValues = {
    username: "",
    email: "",
    password: "",
    roleName: "READER",
  };

  const createUserHandler = async (values) => {
    try {
      const result = await createUser(
        values.username,
        values.email,
        values.password,
        values.roleName,
        accessToken,
      );

      if (typeof onCreate === 'function') {
        onCreate(result); 
      }

      onClose();
    } catch (err) {
      console.error("Грешка при създаване", err.message);
      throw err;
    }
  };

  const { values, changeHendler, submitHendler } = useForm(
    initialValues,
    createUserHandler,
  );

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-box" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2>Нов потребител</h2>
          <button className="modal-close" onClick={onClose}>
            ✕
          </button>
        </div>

        <form onSubmit={submitHendler}>
          <div className="modal-field">
            <label>ПОТРЕБИТЕЛСКО ИМЕ</label>
            <input
              name="username"
              type="text"
              placeholder="User name"
              value={values.username}
              onChange={changeHendler}
            />
          </div>

          {/* Поле: Имейл */}
          <div className="modal-field">
            <label>ИМЕЙЛ</label>
            <input
              name="email"
              type="email"
              placeholder="email@example.com"
              value={values.email}
              onChange={changeHendler}
            />
          </div>

          {/* Поле: Парола */}
          <div className="modal-field">
            <label>ПАРОЛА</label>
            <input
              name="password"
              type="password"
              placeholder="••••••••"
              value={values.password}
              onChange={changeHendler}
            />
          </div>

          {/* Роля */}
          <div className="modal-field">
            <label>РОЛЯ</label>
            <div className="role-grid">
              {Object.keys(roleMapping).map((roleLabel) => (
                <button
                  key={roleLabel}
                  type="button"
                  className={`role-btn ${values.roleName === roleMapping[roleLabel] ? "role-btn--active" : ""}`}
                  onClick={() =>
                    changeHendler({
                      target: {
                        name: "roleName",
                        value: roleMapping[roleLabel],
                      },
                    })
                  }
                >
                  {roleLabel}
                </button>
              ))}
            </div>
          </div>

          {/* Бутони */}
          <div className="modal-footer">
            <button className="btn-cancel" onClick={onClose}>
              Отказ
            </button>
            <button className="btn-create">Създай</button>
          </div>
        </form>
      </div>
    </div>
  );
}
