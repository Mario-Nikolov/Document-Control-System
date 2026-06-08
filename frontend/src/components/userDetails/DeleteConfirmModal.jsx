import { FaUserTimes } from "react-icons/fa";


export default function DeleteConfirmModal({username, onConfirm, onCancel}){
     return (
    <div className="dcm-overlay" onClick={onCancel}>
      <div className="dcm-box" onClick={(e) => e.stopPropagation()}>
        <div className="dcm-icon">
          <FaUserTimes size={22} color="#A32D2D" />
        </div>
        <h3>Премахване на {username}?</h3>
        <p>Потребителят и всичките му данни ще бъдат изтрити завинаги.</p>
        <div className="dcm-btns">
          <button className="dcm-btn-cancel" onClick={onCancel}>Не</button>
          <button className="dcm-btn-danger" onClick={onConfirm}>Да, изтрий</button>
        </div>
      </div>
    </div>
  );
}