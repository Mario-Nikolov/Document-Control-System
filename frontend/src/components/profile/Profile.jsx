import { useNavigate } from "react-router-dom";
import { useAuthContext } from "../../context/AuthContext";
import { useGetAllDocuments } from "../../hooks/useAuth";

export default function Profile() {
  const navigate = useNavigate();
  const { userName, roleName } = useAuthContext();
  const [documents] = useGetAllDocuments();

  const filteredDocuments = documents.filter((doc) =>
    doc.createdByUsername.toLowerCase().includes(userName),
  );

  const logoutHandler = () => {
    localStorage.removeItem("auth");
    navigate("/login");
  };

  const getRoleDisplay = (role) => {
    switch (role) {
      case "ADMIN":
        return "Администратор";
      case "AUTHOR":
        return "Автор";
      case "READER":
        return "Читател";
      case "REVIEWER":
        return "Рецензент";
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
              <span className="number">{filteredDocuments.length}</span>
            </div>
            {/* <div className="stat-item">
              <span className="label">ВЕРСИИ</span>
              <span className="number">3</span>
            </div> */}
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
          {filteredDocuments.length > 0 ? (
            filteredDocuments.map((document) => (
              <div
                key={document.id}
                className="mini-doc-card"
                onClick={() => navigate(`/documents/${document.id}`)}
              >
                <h3>{document.title}</h3>
                <p>{document.description}</p>
                <div className="mini-doc-footer">
                  <span>
                    <i className="fas fa-file-alt" />
                  </span>
                  <span>
                    <i className="far fa-clock" />{" "}
                    {new Date(document.createdAt).toLocaleDateString("bg-BG", {
                      day: "2-digit",
                      month: "2-digit",
                      year: "numeric",
                    })}
                  </span>
                </div>
              </div>
            ))
          ) : (
            <h3>No articles yet!</h3>
          )}
        </section>
      </main>
    </div>
  );
}
