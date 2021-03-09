/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gal.teis.libreriadam;

import gal.teis.excepciones.ValidacionException;

/**
 *
 * @author Esther Ferreiro
 */
public class ValidacionData {

    public static void validarNIF(String nif) throws ValidacionException {
        boolean bad = false;

        if (nif.length() != 9) {

            throw new ValidacionException("NIF", "Error: longitud incorrecta (9 caractares)\n");

        } else if (!nif.substring(0, 8).matches("[0-9]*")) {

            throw new ValidacionException("NIF", "Error: los primeros 8 digitos tienen que ser números\n");
            
        } else if (!nif.substring(8).matches("[aA-zZ]")) {

            throw new ValidacionException("NIF", "Error: el 9 digito tiene que ser una letra\n");
            
        }

    }
}
