package problema2.global.dominio;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class ApiResponse {

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaHora;

    private Integer estatus;

    private Integer codigo;

    private String mensaje;

    private String error;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public Integer getEstatus() {
        return estatus;
    }

    public void setEstatus(Integer estatus) {
        this.estatus = estatus;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

}