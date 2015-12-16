package ni.gob.minsa.laboratorio.web.controllers;

import ni.gob.minsa.laboratorio.domain.estructura.EntidadesAdtvas;
import ni.gob.minsa.laboratorio.domain.muestra.TipoMx;
import ni.gob.minsa.laboratorio.domain.parametros.Imagen;
import ni.gob.minsa.laboratorio.service.ImagenesService;
import ni.gob.minsa.laboratorio.utilities.ConstantsSecurity;
import ni.gob.minsa.laboratorio.utilities.FileMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by FIRSTICT on 12/14/2015.
 * V1.0
 */
@Controller
@RequestMapping("/file")
public class FileUploadController {
    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    LinkedList<FileMeta> files = null;
    FileMeta fileMeta = null;

    @Resource (name = "imagenesService")
    private ImagenesService imagenesService;

    @RequestMapping(value = "init", method = RequestMethod.GET)
    public ModelAndView initSearchForm(HttpServletRequest request) throws Exception {
        ModelAndView mav = new ModelAndView();
        files = new LinkedList<FileMeta>();
        mav.setViewName("/administracion/fileUpload");

        return mav;
    }
    /***************************************************
     * URL: /rest/controller/upload
     * upload(): receives files
     * @param request : MultipartHttpServletRequest auto passed
     * @param response : HttpServletResponse auto passed
     * @return LinkedList<FileMeta> as json format
     ****************************************************/
    @RequestMapping(value="/uploadheader", method = RequestMethod.POST)
    public @ResponseBody
    LinkedList<FileMeta> uploadHeader(MultipartHttpServletRequest request, HttpServletResponse response) {

        //1. build an iterator
        Iterator<String> itr =  request.getFileNames();
        MultipartFile mpf = null;

        //2. get each file
        while(itr.hasNext()){

            //2.1 get next MultipartFile
            mpf = request.getFile(itr.next());
            System.out.println(mpf.getOriginalFilename() +" uploaded! "+files.size());

            //2.2 if files > 10 remove the first from the list
            if(files.size() >= 10)
                files.pop();

            //2.3 create new fileMeta
            fileMeta = new FileMeta();
            fileMeta.setFileName(mpf.getOriginalFilename());
            fileMeta.setFileSize(mpf.getSize()/1024+" Kb");
            fileMeta.setFileType(mpf.getContentType());

            try {
                Imagen imagen = imagenesService.getImagenByName("HEADER_REPORTES");
                fileMeta.setBytes(mpf.getBytes());
                imagen.setBytes(fileMeta.getBytes());
                imagen.setType(fileMeta.getFileType());
                imagen.setNombreArchivo(fileMeta.getFileName());
                imagenesService.saveOrUpdateImagen(imagen);

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //2.4 add to files
            files.add(fileMeta);
        }
        // result will be like this
        // [{"fileName":"app_engine-85x77.png","fileSize":"8 Kb","fileType":"image/png"},...]
        return files;
    }

    /***************************************************
     * URL: /rest/controller/upload
     * upload(): receives files
     * @param request : MultipartHttpServletRequest auto passed
     * @param response : HttpServletResponse auto passed
     * @return LinkedList<FileMeta> as json format
     ****************************************************/
    @RequestMapping(value="/uploadfooter", method = RequestMethod.POST)
    public @ResponseBody
    LinkedList<FileMeta> uploadFooter(MultipartHttpServletRequest request, HttpServletResponse response) {

        //1. build an iterator
        Iterator<String> itr =  request.getFileNames();
        MultipartFile mpf = null;

        //2. get each file
        while(itr.hasNext()){

            //2.1 get next MultipartFile
            mpf = request.getFile(itr.next());
            System.out.println(mpf.getOriginalFilename() +" uploaded! "+files.size());

            //2.2 if files > 10 remove the first from the list
            if(files.size() >= 10)
                files.pop();

            //2.3 create new fileMeta
            fileMeta = new FileMeta();
            fileMeta.setFileName(mpf.getOriginalFilename());
            fileMeta.setFileSize(mpf.getSize()/1024+" Kb");
            fileMeta.setFileType(mpf.getContentType());

            try {
                Imagen imagen = imagenesService.getImagenByName("FOOTER_REPORTES");
                fileMeta.setBytes(mpf.getBytes());
                imagen.setBytes(fileMeta.getBytes());
                imagen.setType(fileMeta.getFileType());
                imagen.setNombreArchivo(fileMeta.getFileName());
                imagenesService.saveOrUpdateImagen(imagen);

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //2.4 add to files
            files.add(fileMeta);
        }
        // result will be like this
        // [{"fileName":"app_engine-85x77.png","fileSize":"8 Kb","fileType":"image/png"},...]
        return files;
    }

    /***************************************************
     * URL: /rest/controller/get/{value}
     * get(): get file as an attachment
     * @param response : passed by the server
     * @param value : value from the URL
     * @return void
     ****************************************************/
    @RequestMapping(value = "/get/{value}", method = RequestMethod.GET)
    public void get(HttpServletResponse response,@PathVariable String value){
        FileMeta getFile = files.get(Integer.parseInt(value));
        try {
            response.setContentType(getFile.getFileType());
            response.setHeader("Content-disposition", "attachment; filename=\""+getFile.getFileName()+"\"");
            FileCopyUtils.copy(getFile.getBytes(), response.getOutputStream());
        }catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/get2/{nombre}", method = RequestMethod.GET)
    public void get2(HttpServletResponse response,@PathVariable String nombre){
        Imagen imagen = imagenesService.getImagenByName(nombre);
        try {
            response.setContentType(imagen.getType());
            response.setHeader("Content-disposition", "attachment; filename=\""+imagen.getNombreArchivo()+"\"");
            FileCopyUtils.copy(imagen.getBytes(), response.getOutputStream());
        }catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET, produces = "application/json")
    public
    @ResponseBody List<Imagen> getAll()throws Exception {
        logger.info("recuperando todas las imágenes");
        return imagenesService.getImagenes();
    }

}
