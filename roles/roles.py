from connexion.resolver import RestyResolver
import connexion


if __name__ == '__main__':
    app = connexion.App(__name__, port=8005, debug=True, specification_dir='swagger/')
    app.add_api('roles_app.yml', resolver=RestyResolver('api'))
    app.run(debug=True)
