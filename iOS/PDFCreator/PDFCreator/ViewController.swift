//
//  ViewController.swift
//  PDFCreator
//
//  Created by 徐力航 on 2024/6/14.
//

import UIKit

class ViewController: UIViewController {
    @IBOutlet weak var selectImagesUIButton: UIButton!
    
    @IBOutlet weak var colorModeUIPickerView: UIPickerView!
    @IBOutlet weak var enableAutoCroppingUISwitch: UISwitch!
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        
    }

    @IBAction func selectImagesUIButton_clicked(_ sender: Any) {
        print("clicked")
    }
    
}

