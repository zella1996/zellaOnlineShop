package com.zella.web.servlet;

import com.zella.domain.Category;
import com.zella.domain.Product;
import com.zella.service.AdminService;
import com.zella.utils.CommonUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminAddProductServlet extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// 接收上传文件

		try {
			Product product = new Product();
			Map<String, Object> map = new HashMap<>();

			DiskFileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			List<FileItem> fileItems = upload.parseRequest(request);
			// 遍历文件项集合
			for (FileItem item : fileItems) {
				// 判断普通表单项/文件上传项
				if (item.isFormField()) {
					// 普通表单项
					String fieldName = item.getFieldName();
					String fieldValue = item.getString("UTF-8");
					map.put(fieldName, fieldValue);

				} else {
					String fileName = item.getName();
					InputStream in = item.getInputStream();
					// String path = this.getServletContext().getRealPath("upload");
					// //此处idea是动态部署，写死路径就好
					// OutputStream out = new FileOutputStream(path + "/" + fileName);
					File uploadFile = new File("D:\\ideaWorkspace\\zellaOnlineShop\\web\\upload\\", fileName);
					OutputStream out = new FileOutputStream(uploadFile);
					IOUtils.copy(in, out);
					in.close();
					out.close();
					item.delete();

					// 添加pimage（路径）到map中
                    map.put("pimage","upload/"+fileName );//D:\ideaWorkspace\zellaOnlineShop\web\products\1\c_0002.jpg
				}
			}

			BeanUtils.populate(product, map);
			// 然后手动封装不完整的product属性
            product.setPid(CommonUtils.getUUID());

            Category category = new Category();
            category.setCid(map.get("cid").toString());
            product.setCategory(category);

            product.setPdate(new Date());
            product.setPflag(0);

			// 此处参数封装完毕，传递product到service层

            AdminService service = new AdminService();
            service.saveProduct(product);

		} catch (FileUploadException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}
