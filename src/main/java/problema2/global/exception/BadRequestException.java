package problema2.global.exception;

public class BadRequestException extends RuntimeException {

    private final Integer codigo;

    private Object[] parametros;

    public BadRequestException(Integer codigo) {
        this.codigo = codigo;
    }

    public BadRequestException(Integer codigo, Object... parametros) {
        this.codigo = codigo;
        this.parametros = parametros;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public Object[] getParametros() {
        return parametros;
    }

}