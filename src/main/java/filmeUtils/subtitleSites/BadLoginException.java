package filmeUtils.subtitleSites;

@SuppressWarnings("serial")
public class BadLoginException extends Exception {

	public BadLoginException() {
		super("Nome de usuário ou senha incorreta");
	}
	
	public BadLoginException(final Exception e) {
		super("Ocorreu um erro na autenticação: ",e);
	}

}
