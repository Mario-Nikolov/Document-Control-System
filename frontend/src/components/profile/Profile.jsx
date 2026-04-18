import { useNavigate } from "react-router-dom";
import { useAuthContext } from "../../context/AuthContext";

export default function Profile() {
    const navigate = useNavigate();
    const {userName,roleName} = useAuthContext();

    const logoutHandler = () => {
      localStorage.removeItem('auth');
      navigate("/login");
    };

    const getRoleDisplay = (role) =>{
      switch(role){
        case "ADMIN" : return "Администратор";
        case "AUTHOR": return "Автор";
        case "READER": return "Читател";
        case "REVIEWER": return "Рецензент"
      }
    };
  return (
    <div className="profile-page-wrapper">
      <main className="profile-card">
        <header className="profile-header">
          <a onClick={() => navigate("/")} className="back-link">
            <i className="fas fa-arrow-left" /> ← Профил
          </a>
        </header>
        <div className="userProfil">
          <section className="user-identity">
            <div className="profile-avatar">
              <i className="fas fa-user" />
            </div>
            <div className="user-details">
              <h1>{userName}</h1>
              <span className="admin-badge">
                <i className="fas fa-cog" /> {getRoleDisplay(roleName)}
              </span>
            </div>
          </section>
          <section className="stats-grid">
            <div className="stat-item">
              <span className="label">ДОКУМЕНТИ</span>
              <span className="number">2</span>
            </div>
            <div className="stat-item">
              <span className="label">ВЕРСИИ</span>
              <span className="number">3</span>
            </div>
          </section>
          <div className="logoutDiv">
            <button className="logout" onClick={logoutHandler}>
              {" "}
              <img src="/images/logout.png" alt="User Avatar" />
              Logout
            </button>
          </div>
        </div>
        <section className="user-docs">
          <h2 className="section-title">МОИТЕ ДОКУМЕНТИ</h2>
          <div className="mini-doc-card">
            <h3>ьоьэь</h3>
            <p>эьэьэьэь</p>
            <div className="mini-doc-footer">
              <span>
                <i className="fas fa-file-alt" /> v0
              </span>
              <span>
                <i className="far fa-clock" /> 14.03.2026 г.
              </span>
            </div>
          </div>
          <div className="mini-doc-card">
            <span className="doc-badge">ЧЕРНОВА</span>
            <h3>Политика за сигурност</h3>
            <p>Вътрешна политика за информационна сигурност</p>
            <div className="mini-doc-footer">
              <span>
                <i className="fas fa-file-alt" /> v3
              </span>
              <span>
                <i className="far fa-clock" /> 14.03.2026 г.
              </span>
            </div>
          </div>
        </section>
      </main>
    </div>
  );
}
