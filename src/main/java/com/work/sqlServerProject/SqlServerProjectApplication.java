package com.work.sqlServerProject;

import com.work.sqlServerProject.Helper.Helper;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.io.IOException;

@SpringBootApplication
public class SqlServerProjectApplication {
	public static Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
	public static void main(String[] args) throws IOException {
		//SpringApplication.run(SqlServerProjectApplication.class, args);
		Helper.createDefPath();
	}

}

