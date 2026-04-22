import { Link, useNavigate } from "react-router-dom";
import { useGetUserHistory } from "../../hooks/useAuth";

function fmt(dt) {
  return new Date(dt).toLocaleString("bg-BG", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
}

export default function UserHistory({ onClose }) {
  const [logs] = useGetUserHistory();
  const navigate = useNavigate();

  const BADGE = {
    LOGIN: "badge-LOGIN",
    LOGOUT: "badge-LOGOUT",
    CREATE: "badge-CREATE",
    DELETE: "badge-DELETE",
    UPDATE: "badge-UPDATE",
  };

  return (
    <div className="hist-overlay">
      <div className="hist-panel">
        <div className="hist-header">
          <button className="hist-back" onClick={onClose}>
            <i className="fas fa-arrow-left" /> ←
          </button>
          <h4>
            {/* <span
              onClick={onClose()}
              style={{ cursor: "pointer", marginRight: "10px" }}
            >
              ←
            </span> */}
            ИСТОРИЯ НА ПОТРЕБИТЕЛЯ
          </h4>
        </div>
        <div className="hist-scroll">
          {logs.map((l) => (
            <div key={l.id} className="hist-row">
              <span className={`hist-badge ${BADGE[l.action] || ""}`}>
                {l.action}
              </span>
              <div className="hist-meta">
                <p className="hist-details">{l.details}</p>
                <div className="hist-sub">
                  <span>{l.username}</span>
                  <span>
                    {l.entityType} #{l.entityId}
                  </span>
                  <span>{fmt(l.createdAt)}</span>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
