import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useChangeUserRole, useGetOneUser } from "../../hooks/useAuth";
import { deleteUser } from "../../api/auth-api";

export default function UserDetailsModal({
  userId,
  onClose,
  onSave,
  //   onDeleteSuccess
}) {
  const navigate = useNavigate();
  const [fetchedUser] = useGetOneUser(userId);
  const [selectedRole, setSelectedRole] = useState("");
  const { changeRoleHandler } = useChangeUserRole();

  const roleMapping = {
    Администратор: "ADMIN",
    Автор: "AUTHOR",
    Рецензент: "REVIEWER",
    Читател: "READER",
  };

  useEffect(() => {
    if (fetchedUser?.role?.name) {
      setSelectedRole(fetchedUser.role.name);
    }
  }, [fetchedUser]);

  const handleClose = () => {
    if (onClose) {
      onClose();
    } else {
      navigate("/"); // Ако нямаме проп за затваряне, се връщаме на началната страница
    }
  };

  const saveHandler = async () => {
    try {
      await changeRoleHandler(userId, selectedRole);
      alert("Ролята беше успешно променена");
      
      onClose();
      window.location.reload();
    } catch (error) {
      console.error("Възникна грешка при смяната на роля");
      throw error;
    }
  };

  const deleteHandler = async () => {
    if (!window.confirm("Наистина ли искате да изтриете този потребител?"))
      return;
    try {
      if (fetchedUser.role.name === "ADMIN") {
        alert("Не можете да изтриете потребител с роля Администратор!");
        return;
      }
      await deleteUser(userId);
      onClose();
      // window.location.reload();
    } catch (err) {
      alert("Неуспешно изтриване. Моля, проверете правата си.");
    }
  };

  // 4. Защита: Ако данните още се зареждат, показваме съобщение (предотвратява белия екран)
  if (!fetchedUser) {
    return <div className="ud-overlay">Зареждане на данни...</div>;
  }

  return (
    <div className="ud-overlay" onClick={handleClose}>
      {/* stopPropagation пречи на клика вътре в кутията да затвори модала */}
      <div className="ud-box" onClick={(e) => e.stopPropagation()}>
        {/* Заглавие */}
        <div className="ud-header">
          <h2>Детайли за потребител</h2>
          <button className="ud-close" onClick={handleClose}>
            ✕
          </button>
        </div>

        {/* Аватар + Име + Роля */}
        <div className="ud-identity">
          <div className="ud-avatar">
            <i className="fas fa-user" />
          </div>
          <div className="ud-name-role">
            <h3>{fetchedUser.username}</h3>
            <span className="ud-role-label">
              <i className="fas fa-tag" />{" "}
              {fetchedUser.role?.name || "Няма роля"}
            </span>
          </div>
        </div>

        {/* Детайли */}
        <div className="ud-details">
          <div className="ud-detail-row">
            <i className="fas fa-at" />
            <span className="ud-detail-key">Потребителско име:</span>
            <span className="ud-detail-value">{fetchedUser.username}</span>
          </div>
          <div className="ud-detail-row">
            <i className="fas fa-envelope" />
            <span className="ud-detail-key">Имейл:</span>
            <span className="ud-detail-value">{fetchedUser.email}</span>
          </div>
        </div>

        {/* Промяна на роля */}
        <div className="ud-role-section">
          <label className="ud-role-title">ПРОМЯНА НА РОЛЯ</label>
          <div className="ud-role-grid">
            {Object.keys(roleMapping).map((roleLabel) => (
              <button
                key={roleLabel}
                type="button"
                /* ПОПРАВЕНО: Използваме selectedRole вместо values.roleName */
                className={`ud-role-btn ${selectedRole === roleMapping[roleLabel] ? "ud-role-btn--active" : ""}`}
                /* ПОПРАВЕНО: Директно сменяме стейта */
                onClick={() => setSelectedRole(roleMapping[roleLabel])}
              >
                {roleLabel}
              </button>
            ))}
          </div>
        </div>

        {/* Footer бутони */}
        <div className="ud-footer">
          <button className="ud-btn-delete" onClick={deleteHandler}>
            <i className="fas fa-trash" /> Изтрий
          </button>
          <button className="ud-btn-save" onClick={saveHandler}>
            Запази промените
          </button>
        </div>
      </div>
    </div>
  );
}
